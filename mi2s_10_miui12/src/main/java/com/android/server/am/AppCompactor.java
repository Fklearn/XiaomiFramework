package com.android.server.am;

import android.app.ActivityThread;
import android.os.Handler;
import android.os.Process;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.ServiceThread;
import com.android.server.job.controllers.JobStatus;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class AppCompactor {
    private static final String COMPACT_ACTION_ANON = "anon";
    private static final int COMPACT_ACTION_ANON_FLAG = 2;
    private static final String COMPACT_ACTION_FILE = "file";
    private static final int COMPACT_ACTION_FILE_FLAG = 1;
    private static final String COMPACT_ACTION_FULL = "all";
    private static final int COMPACT_ACTION_FULL_FLAG = 3;
    private static final String COMPACT_ACTION_NONE = "";
    private static final int COMPACT_ACTION_NONE_FLAG = 4;
    static final int COMPACT_PROCESS_BFGS = 4;
    static final int COMPACT_PROCESS_FULL = 2;
    static final int COMPACT_PROCESS_MSG = 1;
    static final int COMPACT_PROCESS_PERSISTENT = 3;
    static final int COMPACT_PROCESS_SOME = 1;
    static final int COMPACT_SYSTEM_MSG = 2;
    @VisibleForTesting
    static final int DEFAULT_COMPACT_ACTION_1 = 1;
    @VisibleForTesting
    static final int DEFAULT_COMPACT_ACTION_2 = 3;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_FULL_DELTA_RSS_THROTTLE_KB = 8000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_FULL_RSS_THROTTLE_KB = 12000;
    @VisibleForTesting
    static final String DEFAULT_COMPACT_PROC_STATE_THROTTLE = String.valueOf(12);
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_1 = 5000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_2 = 10000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_3 = 500;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_4 = 10000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_5 = 600000;
    @VisibleForTesting
    static final long DEFAULT_COMPACT_THROTTLE_6 = 600000;
    @VisibleForTesting
    static final float DEFAULT_STATSD_SAMPLE_RATE = 0.1f;
    @VisibleForTesting
    static final Boolean DEFAULT_USE_COMPACTION = false;
    @VisibleForTesting
    static final String KEY_COMPACT_ACTION_1 = "compact_action_1";
    @VisibleForTesting
    static final String KEY_COMPACT_ACTION_2 = "compact_action_2";
    @VisibleForTesting
    static final String KEY_COMPACT_FULL_DELTA_RSS_THROTTLE_KB = "compact_full_delta_rss_throttle_kb";
    @VisibleForTesting
    static final String KEY_COMPACT_FULL_RSS_THROTTLE_KB = "compact_full_rss_throttle_kb";
    @VisibleForTesting
    static final String KEY_COMPACT_PROC_STATE_THROTTLE = "compact_proc_state_throttle";
    @VisibleForTesting
    static final String KEY_COMPACT_STATSD_SAMPLE_RATE = "compact_statsd_sample_rate";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_1 = "compact_throttle_1";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_2 = "compact_throttle_2";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_3 = "compact_throttle_3";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_4 = "compact_throttle_4";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_5 = "compact_throttle_5";
    @VisibleForTesting
    static final String KEY_COMPACT_THROTTLE_6 = "compact_throttle_6";
    @VisibleForTesting
    static final String KEY_USE_COMPACTION = "use_compaction";
    /* access modifiers changed from: private */
    public final ActivityManagerService mAm;
    private int mBfgsCompactionCount;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile String mCompactActionFull;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile String mCompactActionSome;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleBFGS;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleFullFull;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleFullSome;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottlePersistent;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleSomeFull;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mCompactThrottleSomeSome;
    private Handler mCompactionHandler;
    final ServiceThread mCompactionThread;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile long mFullAnonRssThrottleKb;
    private int mFullCompactionCount;
    @GuardedBy({"mPhenoypeFlagLock"})
    @VisibleForTesting
    volatile long mFullDeltaRssThrottleKb;
    /* access modifiers changed from: private */
    public Map<Integer, LastCompactionStats> mLastCompactionStats;
    private final DeviceConfig.OnPropertiesChangedListener mOnFlagsChangedListener;
    /* access modifiers changed from: private */
    public final ArrayList<ProcessRecord> mPendingCompactionProcesses;
    private int mPersistentCompactionCount;
    /* access modifiers changed from: private */
    public final Object mPhenotypeFlagLock;
    @GuardedBy({"mPhenoypeFlagLock"})
    @VisibleForTesting
    final Set<Integer> mProcStateThrottle;
    /* access modifiers changed from: private */
    public final Random mRandom;
    private int mSomeCompactionCount;
    @GuardedBy({"mPhenotypeFlagLock"})
    @VisibleForTesting
    volatile float mStatsdSampleRate;
    /* access modifiers changed from: private */
    public PropertyChangedCallbackForTest mTestCallback;
    @GuardedBy({"mPhenotypeFlagLock"})
    private volatile boolean mUseCompaction;

    @VisibleForTesting
    interface PropertyChangedCallbackForTest {
        void onPropertyChanged();
    }

    /* access modifiers changed from: private */
    public native void compactSystem();

    static /* synthetic */ int access$1308(AppCompactor x0) {
        int i = x0.mSomeCompactionCount;
        x0.mSomeCompactionCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$1408(AppCompactor x0) {
        int i = x0.mFullCompactionCount;
        x0.mFullCompactionCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$1508(AppCompactor x0) {
        int i = x0.mPersistentCompactionCount;
        x0.mPersistentCompactionCount = i + 1;
        return i;
    }

    static /* synthetic */ int access$1608(AppCompactor x0) {
        int i = x0.mBfgsCompactionCount;
        x0.mBfgsCompactionCount = i + 1;
        return i;
    }

    public AppCompactor(ActivityManagerService am) {
        this.mPendingCompactionProcesses = new ArrayList<>();
        this.mOnFlagsChangedListener = new DeviceConfig.OnPropertiesChangedListener() {
            public void onPropertiesChanged(DeviceConfig.Properties properties) {
                synchronized (AppCompactor.this.mPhenotypeFlagLock) {
                    for (String name : properties.getKeyset()) {
                        if (AppCompactor.KEY_USE_COMPACTION.equals(name)) {
                            AppCompactor.this.updateUseCompaction();
                        } else {
                            if (!AppCompactor.KEY_COMPACT_ACTION_1.equals(name)) {
                                if (!AppCompactor.KEY_COMPACT_ACTION_2.equals(name)) {
                                    if (!AppCompactor.KEY_COMPACT_THROTTLE_1.equals(name) && !AppCompactor.KEY_COMPACT_THROTTLE_2.equals(name) && !AppCompactor.KEY_COMPACT_THROTTLE_3.equals(name)) {
                                        if (!AppCompactor.KEY_COMPACT_THROTTLE_4.equals(name)) {
                                            if (AppCompactor.KEY_COMPACT_STATSD_SAMPLE_RATE.equals(name)) {
                                                AppCompactor.this.updateStatsdSampleRate();
                                            } else if (AppCompactor.KEY_COMPACT_FULL_RSS_THROTTLE_KB.equals(name)) {
                                                AppCompactor.this.updateFullRssThrottle();
                                            } else if (AppCompactor.KEY_COMPACT_FULL_DELTA_RSS_THROTTLE_KB.equals(name)) {
                                                AppCompactor.this.updateFullDeltaRssThrottle();
                                            } else if (AppCompactor.KEY_COMPACT_PROC_STATE_THROTTLE.equals(name)) {
                                                AppCompactor.this.updateProcStateThrottle();
                                            }
                                        }
                                    }
                                    AppCompactor.this.updateCompactionThrottles();
                                }
                            }
                            AppCompactor.this.updateCompactionActions();
                        }
                    }
                }
                if (AppCompactor.this.mTestCallback != null) {
                    AppCompactor.this.mTestCallback.onPropertyChanged();
                }
            }
        };
        this.mPhenotypeFlagLock = new Object();
        this.mCompactActionSome = compactActionIntToString(1);
        this.mCompactActionFull = compactActionIntToString(3);
        this.mCompactThrottleSomeSome = DEFAULT_COMPACT_THROTTLE_1;
        this.mCompactThrottleSomeFull = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        this.mCompactThrottleFullSome = 500;
        this.mCompactThrottleFullFull = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        this.mCompactThrottleBFGS = 600000;
        this.mCompactThrottlePersistent = 600000;
        this.mUseCompaction = DEFAULT_USE_COMPACTION.booleanValue();
        this.mRandom = new Random();
        this.mStatsdSampleRate = 0.1f;
        this.mFullAnonRssThrottleKb = DEFAULT_COMPACT_FULL_RSS_THROTTLE_KB;
        this.mFullDeltaRssThrottleKb = DEFAULT_COMPACT_FULL_DELTA_RSS_THROTTLE_KB;
        this.mLastCompactionStats = new LinkedHashMap<Integer, LastCompactionStats>() {
            /* access modifiers changed from: protected */
            public boolean removeEldestEntry(Map.Entry eldest) {
                return size() > 100;
            }
        };
        this.mAm = am;
        this.mCompactionThread = new ServiceThread("CompactionThread", -2, true);
        this.mProcStateThrottle = new HashSet();
    }

    @VisibleForTesting
    AppCompactor(ActivityManagerService am, PropertyChangedCallbackForTest callback) {
        this(am);
        this.mTestCallback = callback;
    }

    public void init() {
        DeviceConfig.addOnPropertiesChangedListener("activity_manager", ActivityThread.currentApplication().getMainExecutor(), this.mOnFlagsChangedListener);
        synchronized (this.mPhenotypeFlagLock) {
            updateUseCompaction();
            updateCompactionActions();
            updateCompactionThrottles();
            updateStatsdSampleRate();
            updateFullRssThrottle();
            updateFullDeltaRssThrottle();
            updateProcStateThrottle();
        }
        Process.setThreadGroupAndCpuset(this.mCompactionThread.getThreadId(), 2);
    }

    public boolean useCompaction() {
        boolean z;
        synchronized (this.mPhenotypeFlagLock) {
            z = this.mUseCompaction;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public void dump(PrintWriter pw) {
        pw.println("AppCompactor settings");
        synchronized (this.mPhenotypeFlagLock) {
            pw.println("  use_compaction=" + this.mUseCompaction);
            pw.println("  compact_action_1=" + this.mCompactActionSome);
            pw.println("  compact_action_2=" + this.mCompactActionFull);
            pw.println("  compact_throttle_1=" + this.mCompactThrottleSomeSome);
            pw.println("  compact_throttle_2=" + this.mCompactThrottleSomeFull);
            pw.println("  compact_throttle_3=" + this.mCompactThrottleFullSome);
            pw.println("  compact_throttle_4=" + this.mCompactThrottleFullFull);
            pw.println("  compact_throttle_5=" + this.mCompactThrottleBFGS);
            pw.println("  compact_throttle_6=" + this.mCompactThrottlePersistent);
            pw.println("  compact_statsd_sample_rate=" + this.mStatsdSampleRate);
            pw.println("  compact_full_rss_throttle_kb=" + this.mFullAnonRssThrottleKb);
            pw.println("  compact_full_delta_rss_throttle_kb=" + this.mFullDeltaRssThrottleKb);
            pw.println("  compact_proc_state_throttle=" + Arrays.toString(this.mProcStateThrottle.toArray(new Integer[0])));
            pw.println("  " + this.mSomeCompactionCount + " some, " + this.mFullCompactionCount + " full, " + this.mPersistentCompactionCount + " persistent, " + this.mBfgsCompactionCount + " BFGS compactions.");
            StringBuilder sb = new StringBuilder();
            sb.append("  Tracking last compaction stats for ");
            sb.append(this.mLastCompactionStats.size());
            sb.append(" processes.");
            pw.println(sb.toString());
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public void compactAppSome(ProcessRecord app) {
        app.reqCompactAction = 1;
        this.mPendingCompactionProcesses.add(app);
        Handler handler = this.mCompactionHandler;
        handler.sendMessage(handler.obtainMessage(1, app.setAdj, app.setProcState));
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public void compactAppFull(ProcessRecord app) {
        app.reqCompactAction = 2;
        this.mPendingCompactionProcesses.add(app);
        Handler handler = this.mCompactionHandler;
        handler.sendMessage(handler.obtainMessage(1, app.setAdj, app.setProcState));
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public void compactAppPersistent(ProcessRecord app) {
        app.reqCompactAction = 3;
        this.mPendingCompactionProcesses.add(app);
        Handler handler = this.mCompactionHandler;
        handler.sendMessage(handler.obtainMessage(1, app.curAdj, app.setProcState));
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public boolean shouldCompactPersistent(ProcessRecord app, long now) {
        return app.lastCompactTime == 0 || now - app.lastCompactTime > this.mCompactThrottlePersistent;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public void compactAppBfgs(ProcessRecord app) {
        app.reqCompactAction = 4;
        this.mPendingCompactionProcesses.add(app);
        Handler handler = this.mCompactionHandler;
        handler.sendMessage(handler.obtainMessage(1, app.curAdj, app.setProcState));
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public boolean shouldCompactBFGS(ProcessRecord app, long now) {
        return app.lastCompactTime == 0 || now - app.lastCompactTime > this.mCompactThrottleBFGS;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public void compactAllSystem() {
        if (this.mUseCompaction) {
            Handler handler = this.mCompactionHandler;
            handler.sendMessage(handler.obtainMessage(2));
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mPhenotypeFlagLock"})
    public void updateUseCompaction() {
        this.mUseCompaction = DeviceConfig.getBoolean("activity_manager", KEY_USE_COMPACTION, DEFAULT_USE_COMPACTION.booleanValue());
        if (this.mUseCompaction && !this.mCompactionThread.isAlive()) {
            this.mCompactionThread.start();
            this.mCompactionHandler = new MemCompactionHandler();
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mPhenotypeFlagLock"})
    public void updateCompactionActions() {
        int compactAction1 = DeviceConfig.getInt("activity_manager", KEY_COMPACT_ACTION_1, 1);
        int compactAction2 = DeviceConfig.getInt("activity_manager", KEY_COMPACT_ACTION_2, 3);
        this.mCompactActionSome = compactActionIntToString(compactAction1);
        this.mCompactActionFull = compactActionIntToString(compactAction2);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mPhenotypeFlagLock"})
    public void updateCompactionThrottles() {
        boolean useThrottleDefaults = false;
        String throttleSomeSomeFlag = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_1);
        String throttleSomeFullFlag = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_2);
        String throttleFullSomeFlag = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_3);
        String throttleFullFullFlag = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_4);
        String throttleBFGSFlag = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_5);
        String throttlePersistentFlag = DeviceConfig.getProperty("activity_manager", KEY_COMPACT_THROTTLE_6);
        if (TextUtils.isEmpty(throttleSomeSomeFlag) || TextUtils.isEmpty(throttleSomeFullFlag) || TextUtils.isEmpty(throttleFullSomeFlag) || TextUtils.isEmpty(throttleFullFullFlag) || TextUtils.isEmpty(throttleBFGSFlag) || TextUtils.isEmpty(throttlePersistentFlag)) {
            useThrottleDefaults = true;
        } else {
            try {
                this.mCompactThrottleSomeSome = (long) Integer.parseInt(throttleSomeSomeFlag);
                this.mCompactThrottleSomeFull = (long) Integer.parseInt(throttleSomeFullFlag);
                this.mCompactThrottleFullSome = (long) Integer.parseInt(throttleFullSomeFlag);
                this.mCompactThrottleFullFull = (long) Integer.parseInt(throttleFullFullFlag);
                this.mCompactThrottleBFGS = (long) Integer.parseInt(throttleBFGSFlag);
                this.mCompactThrottlePersistent = (long) Integer.parseInt(throttlePersistentFlag);
            } catch (NumberFormatException e) {
                useThrottleDefaults = true;
            }
        }
        if (useThrottleDefaults) {
            this.mCompactThrottleSomeSome = DEFAULT_COMPACT_THROTTLE_1;
            this.mCompactThrottleSomeFull = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
            this.mCompactThrottleFullSome = 500;
            this.mCompactThrottleFullFull = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
            this.mCompactThrottleBFGS = 600000;
            this.mCompactThrottlePersistent = 600000;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mPhenotypeFlagLock"})
    public void updateStatsdSampleRate() {
        this.mStatsdSampleRate = DeviceConfig.getFloat("activity_manager", KEY_COMPACT_STATSD_SAMPLE_RATE, 0.1f);
        this.mStatsdSampleRate = Math.min(1.0f, Math.max(0.0f, this.mStatsdSampleRate));
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mPhenotypeFlagLock"})
    public void updateFullRssThrottle() {
        this.mFullAnonRssThrottleKb = DeviceConfig.getLong("activity_manager", KEY_COMPACT_FULL_RSS_THROTTLE_KB, DEFAULT_COMPACT_FULL_RSS_THROTTLE_KB);
        if (this.mFullAnonRssThrottleKb < 0) {
            this.mFullAnonRssThrottleKb = DEFAULT_COMPACT_FULL_RSS_THROTTLE_KB;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mPhenotypeFlagLock"})
    public void updateFullDeltaRssThrottle() {
        this.mFullDeltaRssThrottleKb = DeviceConfig.getLong("activity_manager", KEY_COMPACT_FULL_DELTA_RSS_THROTTLE_KB, DEFAULT_COMPACT_FULL_DELTA_RSS_THROTTLE_KB);
        if (this.mFullDeltaRssThrottleKb < 0) {
            this.mFullDeltaRssThrottleKb = DEFAULT_COMPACT_FULL_DELTA_RSS_THROTTLE_KB;
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mPhenotypeFlagLock"})
    public void updateProcStateThrottle() {
        String procStateThrottleString = DeviceConfig.getString("activity_manager", KEY_COMPACT_PROC_STATE_THROTTLE, DEFAULT_COMPACT_PROC_STATE_THROTTLE);
        if (!parseProcStateThrottle(procStateThrottleString)) {
            Slog.w("ActivityManager", "Unable to parse app compact proc state throttle \"" + procStateThrottleString + "\" falling back to default.");
            if (!parseProcStateThrottle(DEFAULT_COMPACT_PROC_STATE_THROTTLE)) {
                Slog.wtf("ActivityManager", "Unable to parse default app compact proc state throttle " + DEFAULT_COMPACT_PROC_STATE_THROTTLE);
            }
        }
    }

    private boolean parseProcStateThrottle(String procStateThrottleString) {
        String[] procStates = TextUtils.split(procStateThrottleString, ",");
        this.mProcStateThrottle.clear();
        int length = procStates.length;
        int i = 0;
        while (i < length) {
            String procState = procStates[i];
            try {
                this.mProcStateThrottle.add(Integer.valueOf(Integer.parseInt(procState)));
                i++;
            } catch (NumberFormatException e) {
                Slog.e("ActivityManager", "Failed to parse default app compaction proc state: " + procState);
                return false;
            }
        }
        return true;
    }

    @VisibleForTesting
    static String compactActionIntToString(int action) {
        if (action == 1) {
            return COMPACT_ACTION_FILE;
        }
        if (action == 2) {
            return COMPACT_ACTION_ANON;
        }
        if (action != 3) {
            return action != 4 ? "" : "";
        }
        return COMPACT_ACTION_FULL;
    }

    private static final class LastCompactionStats {
        private final long[] mRssAfterCompaction;

        LastCompactionStats(long[] rss) {
            this.mRssAfterCompaction = rss;
        }

        /* access modifiers changed from: package-private */
        public long[] getRssAfterCompaction() {
            return this.mRssAfterCompaction;
        }
    }

    private final class MemCompactionHandler extends Handler {
        private MemCompactionHandler() {
            super(AppCompactor.this.mCompactionThread.getLooper());
        }

        /* Debug info: failed to restart local var, previous not found, register: 59 */
        /* JADX WARNING: Code restructure failed: missing block: B:100:0x017a, code lost:
            r9 = r41.getRssAfterCompaction();
            r45 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:101:0x01ab, code lost:
            if (((java.lang.Math.abs(r42[1] - r9[1]) + java.lang.Math.abs(r42[2] - r9[2])) + java.lang.Math.abs(r42[3] - r9[3])) > r11.this$0.mFullDeltaRssThrottleKb) goto L_0x01b0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:102:0x01ad, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:103:0x01ae, code lost:
            r45 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:105:0x01b1, code lost:
            if (r12 == 1) goto L_0x01cf;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:107:0x01b4, code lost:
            if (r12 == 2) goto L_0x01c9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:109:0x01b7, code lost:
            if (r12 == 3) goto L_0x01c3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:111:0x01ba, code lost:
            if (r12 == 4) goto L_0x01bd;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:112:0x01bd, code lost:
            com.android.server.am.AppCompactor.access$1608(r11.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:113:0x01c3, code lost:
            com.android.server.am.AppCompactor.access$1508(r11.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:114:0x01c9, code lost:
            com.android.server.am.AppCompactor.access$1408(r11.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:115:0x01cf, code lost:
            com.android.server.am.AppCompactor.access$1308(r11.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:117:?, code lost:
            r5 = new java.lang.StringBuilder();
            r5.append("Compact ");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:119:0x01e0, code lost:
            if (r12 != 1) goto L_0x0208;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:120:0x01e2, code lost:
            r6 = "some";
         */
        /* JADX WARNING: Code restructure failed: missing block: B:121:0x0208, code lost:
            r6 = "full";
         */
        /* JADX WARNING: Code restructure failed: missing block: B:123:?, code lost:
            r5.append(r6);
            r5.append(": ");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:124:0x0212, code lost:
            r6 = r40;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:126:?, code lost:
            r5.append(r6);
            android.os.Trace.traceBegin(64, r5.toString());
            r47 = android.os.Debug.getZramFreeKb();
            r9 = new java.lang.StringBuilder();
            r9.append("/proc/");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:127:0x0232, code lost:
            r10 = r23;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:129:?, code lost:
            r9.append(r10);
            r9.append("/reclaim");
            r5 = new java.io.FileOutputStream(r9.toString());
            r5.write(r1.getBytes());
            r5.close();
            r9 = android.os.Process.getRss(r10);
            r40 = r5;
            r4 = android.os.SystemClock.uptimeMillis();
            r49 = r4 - r7;
            r51 = android.os.Debug.getZramFreeKb();
            android.util.EventLog.writeEvent(com.android.server.am.EventLogTags.AM_COMPACT, new java.lang.Object[]{java.lang.Integer.valueOf(r10), r6, r1, java.lang.Long.valueOf(r42[0]), java.lang.Long.valueOf(r42[1]), java.lang.Long.valueOf(r42[2]), java.lang.Long.valueOf(r42[3]), java.lang.Long.valueOf(r9[0] - r42[0]), java.lang.Long.valueOf(r9[1] - r42[1]), java.lang.Long.valueOf(r9[2] - r42[2]), java.lang.Long.valueOf(r9[3] - r42[3]), java.lang.Long.valueOf(r49), java.lang.Integer.valueOf(r3), java.lang.Long.valueOf(r45), java.lang.Integer.valueOf(r15), java.lang.Integer.valueOf(r13), java.lang.Long.valueOf(r47), java.lang.Long.valueOf(r51 - r47)});
         */
        /* JADX WARNING: Code restructure failed: missing block: B:131:0x0335, code lost:
            if (com.android.server.am.AppCompactor.access$1700(r11.this$0).nextFloat() >= r11.this$0.mStatsdSampleRate) goto L_0x03b6;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:135:0x0358, code lost:
            r53 = r7;
            r7 = r9;
            r8 = r10;
            r2 = r11;
            r55 = r12;
            r56 = r13;
            r57 = r14;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:137:?, code lost:
            android.util.StatsLog.write(com.android.server.hdmi.HdmiCecKeycode.CEC_KEYCODE_F3_GREEN, r10, r6, r12, r42[0], r42[1], r42[2], r42[3], r9[0], r9[1], r9[2], r9[3], r49, r3, r45, r15, android.app.ActivityManager.processStateAmToProto(r13), r47, r51);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:138:0x038a, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:139:0x038b, code lost:
            r11 = r55;
            r10 = r57;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:141:0x0392, code lost:
            r11 = r55;
            r10 = r57;
            r4 = 64;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:142:0x039a, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:143:0x039b, code lost:
            r53 = r7;
            r8 = r10;
            r2 = r11;
            r56 = r13;
            r58 = r15;
            r11 = r12;
            r10 = r14;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:145:0x03a8, code lost:
            r53 = r7;
            r8 = r10;
            r2 = r11;
            r56 = r13;
            r58 = r15;
            r11 = r12;
            r10 = r14;
            r4 = 64;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:146:0x03b6, code lost:
            r53 = r7;
            r7 = r9;
            r8 = r10;
            r2 = r11;
            r55 = r12;
            r56 = r13;
            r57 = r14;
            r58 = r15;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:148:?, code lost:
            r9 = com.android.server.am.AppCompactor.access$1000(r2.this$0);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:149:0x03c9, code lost:
            monitor-enter(r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:151:?, code lost:
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:152:0x03cd, code lost:
            r10 = r57;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:154:?, code lost:
            r10.lastCompactTime = r4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:157:?, code lost:
            r10.lastCompactAction = r55;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:158:0x03d5, code lost:
            monitor-exit(r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:160:?, code lost:
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:161:0x03df, code lost:
            if (r1.equals(com.android.server.am.AppCompactor.COMPACT_ACTION_FULL) != false) goto L_0x03e9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:163:0x03e7, code lost:
            if (r1.equals(com.android.server.am.AppCompactor.COMPACT_ACTION_ANON) == false) goto L_0x03fb;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:164:0x03e9, code lost:
            com.android.server.am.AppCompactor.access$1200(r2.this$0).put(java.lang.Integer.valueOf(r8), new com.android.server.am.AppCompactor.LastCompactionStats(r7));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:165:0x03fb, code lost:
            r4 = 64;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:166:0x03ff, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:167:0x0400, code lost:
            r11 = r55;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:168:0x0403, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:169:0x0404, code lost:
            r11 = r55;
            r10 = r57;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:171:?, code lost:
            monitor-exit(r9);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:173:?, code lost:
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:174:0x040c, code lost:
            throw r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:175:0x040d, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:177:0x0411, code lost:
            r4 = 64;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:178:0x0415, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:179:0x0417, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:180:0x0418, code lost:
            r11 = r55;
            r10 = r57;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:182:0x041f, code lost:
            r11 = r55;
            r10 = r57;
            r4 = 64;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:183:0x0427, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:184:0x0428, code lost:
            r53 = r7;
            r8 = r10;
            r2 = r11;
            r11 = r12;
            r56 = r13;
            r10 = r14;
            r58 = r15;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:186:0x0434, code lost:
            r53 = r7;
            r8 = r10;
            r2 = r11;
            r11 = r12;
            r56 = r13;
            r10 = r14;
            r58 = r15;
            r4 = 64;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:187:0x0441, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:188:0x0442, code lost:
            r53 = r7;
            r2 = r11;
            r11 = r12;
            r56 = r13;
            r10 = r14;
            r58 = r15;
            r8 = r23;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:190:0x044f, code lost:
            r53 = r7;
            r2 = r11;
            r11 = r12;
            r56 = r13;
            r10 = r14;
            r58 = r15;
            r8 = r23;
            r4 = 64;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:191:0x045d, code lost:
            r0 = th;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:192:0x045e, code lost:
            r53 = r7;
            r2 = r11;
            r11 = r12;
            r56 = r13;
            r10 = r14;
            r58 = r15;
            r8 = r23;
            r6 = r40;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:193:0x046b, code lost:
            android.os.Trace.traceEnd(64);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:194:0x0470, code lost:
            throw r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:196:0x0472, code lost:
            r53 = r7;
            r2 = r11;
            r11 = r12;
            r56 = r13;
            r10 = r14;
            r58 = r15;
            r8 = r23;
            r6 = r40;
            r4 = 64;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:208:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:209:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:210:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:211:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x007c, code lost:
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:0x007f, code lost:
            if (r11 != 0) goto L_0x0082;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x0081, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x0087, code lost:
            if (r5 == 0) goto L_0x00f4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x008a, code lost:
            if (r12 != 1) goto L_0x00af;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:30:0x008c, code lost:
            if (r3 != 1) goto L_0x009c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x008e, code lost:
            r23 = r11;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x0099, code lost:
            if ((r7 - r5) < r1.this$0.mCompactThrottleSomeSome) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x009c, code lost:
            r23 = r11;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:0x009f, code lost:
            if (r3 != 2) goto L_0x00ad;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:37:0x00aa, code lost:
            if ((r7 - r5) >= r1.this$0.mCompactThrottleSomeFull) goto L_0x00ad;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:38:0x00ad, code lost:
            r11 = r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:39:0x00af, code lost:
            r23 = r11;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x00b2, code lost:
            if (r12 != 2) goto L_0x00d4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00b5, code lost:
            if (r3 != 1) goto L_0x00c1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x00bf, code lost:
            if ((r7 - r5) < r1.this$0.mCompactThrottleFullSome) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c2, code lost:
            if (r3 != 2) goto L_0x00d1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:48:0x00ce, code lost:
            if ((r7 - r5) >= r59.this$0.mCompactThrottleFullFull) goto L_0x00d1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x00d1, code lost:
            r11 = r59;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:0x00d5, code lost:
            if (r12 != 3) goto L_0x00e4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x00d7, code lost:
            r11 = r59;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x00e1, code lost:
            if ((r7 - r5) >= r11.this$0.mCompactThrottlePersistent) goto L_0x00f7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x00e3, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x00e4, code lost:
            r11 = r59;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x00e7, code lost:
            if (r12 != 4) goto L_0x00f7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x00f1, code lost:
            if ((r7 - r5) >= r11.this$0.mCompactThrottleBFGS) goto L_0x00f7;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x00f3, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x00f4, code lost:
            r23 = r11;
            r11 = r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x00f8, code lost:
            if (r12 == 1) goto L_0x010b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x00fb, code lost:
            if (r12 == 2) goto L_0x0106;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:66:0x00fe, code lost:
            if (r12 == 3) goto L_0x0106;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:68:0x0101, code lost:
            if (r12 == 4) goto L_0x0106;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:69:0x0103, code lost:
            r1 = "";
         */
        /* JADX WARNING: Code restructure failed: missing block: B:70:0x0106, code lost:
            r1 = r11.this$0.mCompactActionFull;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:71:0x010b, code lost:
            r1 = r11.this$0.mCompactActionSome;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:73:0x0116, code lost:
            if ("".equals(r1) == false) goto L_0x0119;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:74:0x0118, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:76:0x0125, code lost:
            if (r11.this$0.mProcStateThrottle.contains(java.lang.Integer.valueOf(r13)) == false) goto L_0x0128;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:77:0x0127, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:78:0x0128, code lost:
            r42 = android.os.Process.getRss(r23);
            r43 = r42[2];
         */
        /* JADX WARNING: Code restructure failed: missing block: B:79:0x0134, code lost:
            if (r42[0] != 0) goto L_0x014b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:81:0x013b, code lost:
            if (r42[1] != 0) goto L_0x014b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:83:0x0141, code lost:
            if (r42[2] != 0) goto L_0x014b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:85:0x0148, code lost:
            if (r42[3] != 0) goto L_0x014b;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:86:0x014a, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:88:0x0151, code lost:
            if (r1.equals(com.android.server.am.AppCompactor.COMPACT_ACTION_FULL) != false) goto L_0x015f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:90:0x0159, code lost:
            if (r1.equals(com.android.server.am.AppCompactor.COMPACT_ACTION_ANON) == false) goto L_0x015c;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:91:0x015c, code lost:
            r45 = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:93:0x0165, code lost:
            if (r11.this$0.mFullAnonRssThrottleKb <= 0) goto L_0x0170;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:95:0x016d, code lost:
            if (r43 >= r11.this$0.mFullAnonRssThrottleKb) goto L_0x0170;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:96:0x016f, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:97:0x0170, code lost:
            if (r41 == null) goto L_0x01ae;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:99:0x0178, code lost:
            if (r11.this$0.mFullDeltaRssThrottleKb <= 0) goto L_0x01ae;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r60) {
            /*
                r59 = this;
                r1 = r59
                r2 = r60
                int r0 = r2.what
                r3 = 64
                r5 = 2
                r6 = 1
                if (r0 == r6) goto L_0x0021
                if (r0 == r5) goto L_0x0011
                r2 = r1
                goto L_0x0487
            L_0x0011:
                java.lang.String r0 = "compactSystem"
                android.os.Trace.traceBegin(r3, r0)
                com.android.server.am.AppCompactor r0 = com.android.server.am.AppCompactor.this
                r0.compactSystem()
                android.os.Trace.traceEnd(r3)
                r2 = r1
                goto L_0x0487
            L_0x0021:
                long r7 = android.os.SystemClock.uptimeMillis()
                int r15 = r2.arg1
                int r13 = r2.arg2
                com.android.server.am.AppCompactor r0 = com.android.server.am.AppCompactor.this
                com.android.server.am.ActivityManagerService r9 = r0.mAm
                monitor-enter(r9)
                com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0488 }
                com.android.server.am.AppCompactor r0 = com.android.server.am.AppCompactor.this     // Catch:{ all -> 0x0488 }
                java.util.ArrayList r0 = r0.mPendingCompactionProcesses     // Catch:{ all -> 0x0488 }
                r10 = 0
                java.lang.Object r0 = r0.remove(r10)     // Catch:{ all -> 0x0488 }
                com.android.server.am.ProcessRecord r0 = (com.android.server.am.ProcessRecord) r0     // Catch:{ all -> 0x0488 }
                r14 = r0
                int r0 = r14.reqCompactAction     // Catch:{ all -> 0x0488 }
                r12 = r0
                int r0 = r14.pid     // Catch:{ all -> 0x0488 }
                r11 = r0
                java.lang.String r0 = r14.processName     // Catch:{ all -> 0x0488 }
                r40 = r0
                if (r12 == r6) goto L_0x004f
                if (r12 != r5) goto L_0x0064
            L_0x004f:
                int r0 = r14.setAdj     // Catch:{ all -> 0x0488 }
                r3 = 200(0xc8, float:2.8E-43)
                if (r0 > r3) goto L_0x0064
                monitor-exit(r9)     // Catch:{ all -> 0x005a }
                com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
                return
            L_0x005a:
                r0 = move-exception
                r2 = r1
                r53 = r7
                r56 = r13
                r58 = r15
                goto L_0x0490
            L_0x0064:
                int r0 = r14.lastCompactAction     // Catch:{ all -> 0x0488 }
                r3 = r0
                long r5 = r14.lastCompactTime     // Catch:{ all -> 0x0488 }
                com.android.server.am.AppCompactor r0 = com.android.server.am.AppCompactor.this     // Catch:{ all -> 0x0488 }
                java.util.Map r0 = r0.mLastCompactionStats     // Catch:{ all -> 0x0488 }
                java.lang.Integer r4 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x0488 }
                java.lang.Object r0 = r0.remove(r4)     // Catch:{ all -> 0x0488 }
                com.android.server.am.AppCompactor$LastCompactionStats r0 = (com.android.server.am.AppCompactor.LastCompactionStats) r0     // Catch:{ all -> 0x0488 }
                r41 = r0
                monitor-exit(r9)     // Catch:{ all -> 0x0488 }
                com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
                if (r11 != 0) goto L_0x0082
                return
            L_0x0082:
                r18 = 0
                int r0 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
                r4 = 3
                if (r0 == 0) goto L_0x00f4
                r0 = 1
                if (r12 != r0) goto L_0x00af
                if (r3 != r0) goto L_0x009c
                r0 = r4
                long r20 = r7 - r5
                com.android.server.am.AppCompactor r0 = com.android.server.am.AppCompactor.this
                r23 = r11
                long r10 = r0.mCompactThrottleSomeSome
                int r0 = (r20 > r10 ? 1 : (r20 == r10 ? 0 : -1))
                if (r0 < 0) goto L_0x00ac
                goto L_0x009e
            L_0x009c:
                r23 = r11
            L_0x009e:
                r0 = 2
                if (r3 != r0) goto L_0x00ad
                r10 = 3
                long r16 = r7 - r5
                com.android.server.am.AppCompactor r11 = com.android.server.am.AppCompactor.this
                long r9 = r11.mCompactThrottleSomeFull
                int r9 = (r16 > r9 ? 1 : (r16 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x00ad
            L_0x00ac:
                return
            L_0x00ad:
                r11 = r1
                goto L_0x00f7
            L_0x00af:
                r23 = r11
                r0 = 2
                if (r12 != r0) goto L_0x00d4
                r4 = 1
                if (r3 != r4) goto L_0x00c1
                long r9 = r7 - r5
                com.android.server.am.AppCompactor r11 = com.android.server.am.AppCompactor.this
                long r0 = r11.mCompactThrottleFullSome
                int r0 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r0 < 0) goto L_0x00d0
            L_0x00c1:
                r0 = 2
                if (r3 != r0) goto L_0x00d1
                long r9 = r7 - r5
                r1 = r59
                com.android.server.am.AppCompactor r11 = com.android.server.am.AppCompactor.this
                long r0 = r11.mCompactThrottleFullFull
                int r0 = (r9 > r0 ? 1 : (r9 == r0 ? 0 : -1))
                if (r0 >= 0) goto L_0x00d1
            L_0x00d0:
                return
            L_0x00d1:
                r11 = r59
                goto L_0x00f7
            L_0x00d4:
                r0 = 3
                if (r12 != r0) goto L_0x00e4
                long r0 = r7 - r5
                r11 = r59
                com.android.server.am.AppCompactor r9 = com.android.server.am.AppCompactor.this
                long r9 = r9.mCompactThrottlePersistent
                int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r0 >= 0) goto L_0x00f7
                return
            L_0x00e4:
                r11 = r59
                r0 = 4
                if (r12 != r0) goto L_0x00f7
                long r0 = r7 - r5
                com.android.server.am.AppCompactor r9 = com.android.server.am.AppCompactor.this
                long r9 = r9.mCompactThrottleBFGS
                int r0 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1))
                if (r0 >= 0) goto L_0x00f7
                return
            L_0x00f4:
                r23 = r11
                r11 = r1
            L_0x00f7:
                r0 = 1
                if (r12 == r0) goto L_0x010b
                r0 = 2
                if (r12 == r0) goto L_0x0106
                r1 = 3
                if (r12 == r1) goto L_0x0106
                r1 = 4
                if (r12 == r1) goto L_0x0106
                java.lang.String r1 = ""
                goto L_0x0110
            L_0x0106:
                com.android.server.am.AppCompactor r1 = com.android.server.am.AppCompactor.this
                java.lang.String r1 = r1.mCompactActionFull
                goto L_0x0110
            L_0x010b:
                com.android.server.am.AppCompactor r1 = com.android.server.am.AppCompactor.this
                java.lang.String r1 = r1.mCompactActionSome
            L_0x0110:
                java.lang.String r9 = ""
                boolean r9 = r9.equals(r1)
                if (r9 == 0) goto L_0x0119
                return
            L_0x0119:
                com.android.server.am.AppCompactor r9 = com.android.server.am.AppCompactor.this
                java.util.Set<java.lang.Integer> r9 = r9.mProcStateThrottle
                java.lang.Integer r10 = java.lang.Integer.valueOf(r13)
                boolean r9 = r9.contains(r10)
                if (r9 == 0) goto L_0x0128
                return
            L_0x0128:
                long[] r42 = android.os.Process.getRss(r23)
                r0 = 2
                r43 = r42[r0]
                r9 = 0
                r16 = r42[r9]
                int r9 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
                if (r9 != 0) goto L_0x014b
                r4 = 1
                r9 = r42[r4]
                int r9 = (r9 > r18 ? 1 : (r9 == r18 ? 0 : -1))
                if (r9 != 0) goto L_0x014b
                r9 = r42[r0]
                int r9 = (r9 > r18 ? 1 : (r9 == r18 ? 0 : -1))
                if (r9 != 0) goto L_0x014b
                r9 = 3
                r16 = r42[r9]
                int r9 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
                if (r9 != 0) goto L_0x014b
                return
            L_0x014b:
                java.lang.String r9 = "all"
                boolean r9 = r1.equals(r9)
                if (r9 != 0) goto L_0x015f
                java.lang.String r9 = "anon"
                boolean r9 = r1.equals(r9)
                if (r9 == 0) goto L_0x015c
                goto L_0x015f
            L_0x015c:
                r45 = r5
                goto L_0x01b0
            L_0x015f:
                com.android.server.am.AppCompactor r9 = com.android.server.am.AppCompactor.this
                long r9 = r9.mFullAnonRssThrottleKb
                int r9 = (r9 > r18 ? 1 : (r9 == r18 ? 0 : -1))
                if (r9 <= 0) goto L_0x0170
                com.android.server.am.AppCompactor r9 = com.android.server.am.AppCompactor.this
                long r9 = r9.mFullAnonRssThrottleKb
                int r9 = (r43 > r9 ? 1 : (r43 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x0170
                return
            L_0x0170:
                if (r41 == 0) goto L_0x01ae
                com.android.server.am.AppCompactor r9 = com.android.server.am.AppCompactor.this
                long r9 = r9.mFullDeltaRssThrottleKb
                int r9 = (r9 > r18 ? 1 : (r9 == r18 ? 0 : -1))
                if (r9 <= 0) goto L_0x01ae
                long[] r9 = r41.getRssAfterCompaction()
                r4 = 1
                r16 = r42[r4]
                r18 = r9[r4]
                long r16 = r16 - r18
                long r16 = java.lang.Math.abs(r16)
                r0 = 2
                r18 = r42[r0]
                r24 = r9[r0]
                long r18 = r18 - r24
                long r18 = java.lang.Math.abs(r18)
                long r16 = r16 + r18
                r10 = 3
                r18 = r42[r10]
                r24 = r9[r10]
                long r18 = r18 - r24
                long r18 = java.lang.Math.abs(r18)
                long r16 = r16 + r18
                com.android.server.am.AppCompactor r10 = com.android.server.am.AppCompactor.this
                r45 = r5
                long r4 = r10.mFullDeltaRssThrottleKb
                int r4 = (r16 > r4 ? 1 : (r16 == r4 ? 0 : -1))
                if (r4 > 0) goto L_0x01b0
                return
            L_0x01ae:
                r45 = r5
            L_0x01b0:
                r4 = 1
                if (r12 == r4) goto L_0x01cf
                r0 = 2
                if (r12 == r0) goto L_0x01c9
                r5 = 3
                if (r12 == r5) goto L_0x01c3
                r5 = 4
                if (r12 == r5) goto L_0x01bd
                goto L_0x01d5
            L_0x01bd:
                com.android.server.am.AppCompactor r5 = com.android.server.am.AppCompactor.this
                com.android.server.am.AppCompactor.access$1608(r5)
                goto L_0x01d5
            L_0x01c3:
                com.android.server.am.AppCompactor r5 = com.android.server.am.AppCompactor.this
                com.android.server.am.AppCompactor.access$1508(r5)
                goto L_0x01d5
            L_0x01c9:
                com.android.server.am.AppCompactor r5 = com.android.server.am.AppCompactor.this
                com.android.server.am.AppCompactor.access$1408(r5)
                goto L_0x01d5
            L_0x01cf:
                com.android.server.am.AppCompactor r5 = com.android.server.am.AppCompactor.this
                com.android.server.am.AppCompactor.access$1308(r5)
            L_0x01d5:
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0471, all -> 0x045d }
                r5.<init>()     // Catch:{ Exception -> 0x0471, all -> 0x045d }
                java.lang.String r6 = "Compact "
                r5.append(r6)     // Catch:{ Exception -> 0x0471, all -> 0x045d }
                r4 = 1
                if (r12 != r4) goto L_0x0208
                java.lang.String r6 = "some"
                goto L_0x020a
            L_0x01e6:
                r0 = move-exception
                r53 = r7
                r2 = r11
                r11 = r12
                r56 = r13
                r10 = r14
                r58 = r15
                r8 = r23
                r6 = r40
                goto L_0x046b
            L_0x01f6:
                r0 = move-exception
                r53 = r7
                r2 = r11
                r11 = r12
                r56 = r13
                r10 = r14
                r58 = r15
                r8 = r23
                r6 = r40
                r4 = 64
                goto L_0x0481
            L_0x0208:
                java.lang.String r6 = "full"
            L_0x020a:
                r5.append(r6)     // Catch:{ Exception -> 0x0471, all -> 0x045d }
                java.lang.String r6 = ": "
                r5.append(r6)     // Catch:{ Exception -> 0x0471, all -> 0x045d }
                r6 = r40
                r5.append(r6)     // Catch:{ Exception -> 0x044e, all -> 0x0441 }
                java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x044e, all -> 0x0441 }
                r9 = 64
                android.os.Trace.traceBegin(r9, r5)     // Catch:{ Exception -> 0x044e, all -> 0x0441 }
                long r9 = android.os.Debug.getZramFreeKb()     // Catch:{ Exception -> 0x044e, all -> 0x0441 }
                r47 = r9
                java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x044e, all -> 0x0441 }
                java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x044e, all -> 0x0441 }
                r9.<init>()     // Catch:{ Exception -> 0x044e, all -> 0x0441 }
                java.lang.String r10 = "/proc/"
                r9.append(r10)     // Catch:{ Exception -> 0x044e, all -> 0x0441 }
                r10 = r23
                r9.append(r10)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                java.lang.String r0 = "/reclaim"
                r9.append(r0)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                java.lang.String r0 = r9.toString()     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r5.<init>(r0)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                byte[] r0 = r1.getBytes()     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r5.write(r0)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r5.close()     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                long[] r0 = android.os.Process.getRss(r10)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r9 = r0
                long r17 = android.os.SystemClock.uptimeMillis()     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r49 = r17
                r40 = r5
                r4 = r49
                long r49 = r4 - r7
                long r17 = android.os.Debug.getZramFreeKb()     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r51 = r17
                r0 = 18
                java.lang.Object[] r0 = new java.lang.Object[r0]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                java.lang.Integer r19 = java.lang.Integer.valueOf(r10)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r22 = 0
                r0[r22] = r19     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r17 = 1
                r0[r17] = r6     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r16 = 2
                r0[r16] = r1     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r23 = r42[r22]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                java.lang.Long r19 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r21 = 3
                r0[r21] = r19     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r17 = 1
                r23 = r42[r17]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                java.lang.Long r19 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r20 = 4
                r0[r20] = r19     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 5
                r16 = 2
                r23 = r42[r16]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                java.lang.Long r20 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 6
                r20 = 3
                r23 = r42[r20]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                java.lang.Long r20 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 7
                r20 = 0
                r23 = r9[r20]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r25 = r42[r20]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                long r23 = r23 - r25
                java.lang.Long r20 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 8
                r17 = 1
                r23 = r9[r17]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r25 = r42[r17]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                long r23 = r23 - r25
                java.lang.Long r20 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 9
                r16 = 2
                r23 = r9[r16]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r25 = r42[r16]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                long r23 = r23 - r25
                java.lang.Long r20 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 10
                r20 = 3
                r23 = r9[r20]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r25 = r42[r20]     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                long r23 = r23 - r25
                java.lang.Long r20 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 11
                java.lang.Long r20 = java.lang.Long.valueOf(r49)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 12
                java.lang.Integer r20 = java.lang.Integer.valueOf(r3)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 13
                java.lang.Long r20 = java.lang.Long.valueOf(r45)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 14
                java.lang.Integer r20 = java.lang.Integer.valueOf(r15)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 15
                java.lang.Integer r20 = java.lang.Integer.valueOf(r13)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 16
                java.lang.Long r20 = java.lang.Long.valueOf(r47)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r19 = 17
                long r23 = r51 - r47
                java.lang.Long r20 = java.lang.Long.valueOf(r23)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r0[r19] = r20     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                r2 = 30063(0x756f, float:4.2127E-41)
                android.util.EventLog.writeEvent(r2, r0)     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                com.android.server.am.AppCompactor r0 = com.android.server.am.AppCompactor.this     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                java.util.Random r0 = r0.mRandom     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                float r0 = r0.nextFloat()     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                com.android.server.am.AppCompactor r2 = com.android.server.am.AppCompactor.this     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                float r2 = r2.mStatsdSampleRate     // Catch:{ Exception -> 0x0433, all -> 0x0427 }
                int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                if (r0 >= 0) goto L_0x03b6
                r2 = 115(0x73, float:1.61E-43)
                r0 = 0
                r18 = r42[r0]     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                r0 = 1
                r23 = r42[r0]     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                r0 = 2
                r25 = r42[r0]     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                r16 = 3
                r27 = r42[r16]     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                r16 = 0
                r29 = r9[r16]     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                r16 = 1
                r31 = r9[r16]     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                r0 = 2
                r33 = r9[r0]     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                r0 = 3
                r35 = r9[r0]     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                int r0 = android.app.ActivityManager.processStateAmToProto(r13)     // Catch:{ Exception -> 0x03a7, all -> 0x039a }
                r53 = r7
                r7 = r9
                r9 = r2
                r2 = r10
                r8 = r2
                r2 = r11
                r11 = r6
                r55 = r12
                r56 = r13
                r57 = r14
                r13 = r18
                r58 = r15
                r15 = r23
                r17 = r25
                r19 = r27
                r21 = r29
                r23 = r31
                r25 = r33
                r27 = r35
                r29 = r49
                r31 = r3
                r32 = r45
                r34 = r58
                r35 = r0
                r36 = r47
                r38 = r51
                android.util.StatsLog.write(r9, r10, r11, r12, r13, r15, r17, r19, r21, r23, r25, r27, r29, r31, r32, r34, r35, r36, r38)     // Catch:{ Exception -> 0x0391, all -> 0x038a }
                goto L_0x03c3
            L_0x038a:
                r0 = move-exception
                r11 = r55
                r10 = r57
                goto L_0x046b
            L_0x0391:
                r0 = move-exception
                r11 = r55
                r10 = r57
                r4 = 64
                goto L_0x0481
            L_0x039a:
                r0 = move-exception
                r53 = r7
                r8 = r10
                r2 = r11
                r56 = r13
                r58 = r15
                r11 = r12
                r10 = r14
                goto L_0x046b
            L_0x03a7:
                r0 = move-exception
                r53 = r7
                r8 = r10
                r2 = r11
                r56 = r13
                r58 = r15
                r11 = r12
                r10 = r14
                r4 = 64
                goto L_0x0481
            L_0x03b6:
                r53 = r7
                r7 = r9
                r8 = r10
                r2 = r11
                r55 = r12
                r56 = r13
                r57 = r14
                r58 = r15
            L_0x03c3:
                com.android.server.am.AppCompactor r0 = com.android.server.am.AppCompactor.this     // Catch:{ Exception -> 0x041e, all -> 0x0417 }
                com.android.server.am.ActivityManagerService r9 = r0.mAm     // Catch:{ Exception -> 0x041e, all -> 0x0417 }
                monitor-enter(r9)     // Catch:{ Exception -> 0x041e, all -> 0x0417 }
                com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0403 }
                r10 = r57
                r10.lastCompactTime = r4     // Catch:{ all -> 0x03ff }
                r11 = r55
                r10.lastCompactAction = r11     // Catch:{ all -> 0x0415 }
                monitor-exit(r9)     // Catch:{ all -> 0x0415 }
                com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                java.lang.String r0 = "all"
                boolean r0 = r1.equals(r0)     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                if (r0 != 0) goto L_0x03e9
                java.lang.String r0 = "anon"
                boolean r0 = r1.equals(r0)     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                if (r0 == 0) goto L_0x03fb
            L_0x03e9:
                com.android.server.am.AppCompactor r0 = com.android.server.am.AppCompactor.this     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                java.util.Map r0 = r0.mLastCompactionStats     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                java.lang.Integer r9 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                com.android.server.am.AppCompactor$LastCompactionStats r12 = new com.android.server.am.AppCompactor$LastCompactionStats     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                r12.<init>(r7)     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                r0.put(r9, r12)     // Catch:{ Exception -> 0x0410, all -> 0x040d }
            L_0x03fb:
                r4 = 64
                goto L_0x0482
            L_0x03ff:
                r0 = move-exception
                r11 = r55
                goto L_0x0408
            L_0x0403:
                r0 = move-exception
                r11 = r55
                r10 = r57
            L_0x0408:
                monitor-exit(r9)     // Catch:{ all -> 0x0415 }
                com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()     // Catch:{ Exception -> 0x0410, all -> 0x040d }
                throw r0     // Catch:{ Exception -> 0x0410, all -> 0x040d }
            L_0x040d:
                r0 = move-exception
                goto L_0x046b
            L_0x0410:
                r0 = move-exception
                r4 = 64
                goto L_0x0481
            L_0x0415:
                r0 = move-exception
                goto L_0x0408
            L_0x0417:
                r0 = move-exception
                r11 = r55
                r10 = r57
                goto L_0x046b
            L_0x041e:
                r0 = move-exception
                r11 = r55
                r10 = r57
                r4 = 64
                goto L_0x0481
            L_0x0427:
                r0 = move-exception
                r53 = r7
                r8 = r10
                r2 = r11
                r11 = r12
                r56 = r13
                r10 = r14
                r58 = r15
                goto L_0x046b
            L_0x0433:
                r0 = move-exception
                r53 = r7
                r8 = r10
                r2 = r11
                r11 = r12
                r56 = r13
                r10 = r14
                r58 = r15
                r4 = 64
                goto L_0x0481
            L_0x0441:
                r0 = move-exception
                r53 = r7
                r2 = r11
                r11 = r12
                r56 = r13
                r10 = r14
                r58 = r15
                r8 = r23
                goto L_0x046b
            L_0x044e:
                r0 = move-exception
                r53 = r7
                r2 = r11
                r11 = r12
                r56 = r13
                r10 = r14
                r58 = r15
                r8 = r23
                r4 = 64
                goto L_0x0481
            L_0x045d:
                r0 = move-exception
                r53 = r7
                r2 = r11
                r11 = r12
                r56 = r13
                r10 = r14
                r58 = r15
                r8 = r23
                r6 = r40
            L_0x046b:
                r4 = 64
                android.os.Trace.traceEnd(r4)
                throw r0
            L_0x0471:
                r0 = move-exception
                r53 = r7
                r2 = r11
                r11 = r12
                r56 = r13
                r10 = r14
                r58 = r15
                r8 = r23
                r6 = r40
                r4 = 64
            L_0x0481:
            L_0x0482:
                android.os.Trace.traceEnd(r4)
            L_0x0487:
                return
            L_0x0488:
                r0 = move-exception
                r2 = r1
                r53 = r7
                r56 = r13
                r58 = r15
            L_0x0490:
                monitor-exit(r9)     // Catch:{ all -> 0x0495 }
                com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
                throw r0
            L_0x0495:
                r0 = move-exception
                goto L_0x0490
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AppCompactor.MemCompactionHandler.handleMessage(android.os.Message):void");
        }
    }
}
