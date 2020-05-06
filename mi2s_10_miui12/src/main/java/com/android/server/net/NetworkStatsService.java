package com.android.server.net;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.net.ConnectivityManager;
import android.net.DataUsageRequest;
import android.net.INetworkManagementEventObserver;
import android.net.INetworkStatsService;
import android.net.INetworkStatsSession;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkIdentity;
import android.net.NetworkStack;
import android.net.NetworkState;
import android.net.NetworkStats;
import android.net.NetworkStatsHistory;
import android.net.NetworkTemplate;
import android.os.BestClock;
import android.os.Binder;
import android.os.DropBoxManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.SubscriptionPlan;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.net.VpnInfo;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FileRotator;
import com.android.internal.util.Preconditions;
import com.android.server.EventLogTags;
import com.android.server.LocalServices;
import com.android.server.NetworkManagementService;
import com.android.server.NetworkManagementSocketTagger;
import com.android.server.job.controllers.JobStatus;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;

public class NetworkStatsService extends INetworkStatsService.Stub {
    @VisibleForTesting
    public static final String ACTION_NETWORK_STATS_POLL = "com.android.server.action.NETWORK_STATS_POLL";
    public static final String ACTION_NETWORK_STATS_UPDATED = "com.android.server.action.NETWORK_STATS_UPDATED";
    private static final int DUMP_STATS_SESSION_COUNT = 20;
    private static final int FLAG_PERSIST_ALL = 3;
    private static final int FLAG_PERSIST_FORCE = 256;
    private static final int FLAG_PERSIST_NETWORK = 1;
    private static final int FLAG_PERSIST_UID = 2;
    static final boolean LOGD = Log.isLoggable(TAG, 3);
    static final boolean LOGV = Log.isLoggable(TAG, 2);
    private static final int MSG_PERFORM_POLL = 1;
    private static final int MSG_PERFORM_POLL_REGISTER_ALERT = 2;
    private static final int PERFORM_POLL_DELAY_MS = 1000;
    private static final long POLL_RATE_LIMIT_MS = 15000;
    private static final String PREFIX_DEV = "dev";
    private static final String PREFIX_UID = "uid";
    private static final String PREFIX_UID_TAG = "uid_tag";
    private static final String PREFIX_XT = "xt";
    static final String TAG = "NetworkStats";
    private static final String TAG_NETSTATS_ERROR = "netstats_error";
    private static int TYPE_RX_BYTES = 0;
    private static int TYPE_RX_PACKETS = 0;
    private static int TYPE_TCP_RX_PACKETS = 0;
    private static int TYPE_TCP_TX_PACKETS = 0;
    private static int TYPE_TX_BYTES = 0;
    private static int TYPE_TX_PACKETS = 0;
    public static final String VT_INTERFACE = "vt_data0";
    @GuardedBy({"mStatsLock"})
    private String mActiveIface;
    @GuardedBy({"mStatsLock"})
    private final ArrayMap<String, NetworkIdentitySet> mActiveIfaces = new ArrayMap<>();
    private SparseIntArray mActiveUidCounterSet = new SparseIntArray();
    @GuardedBy({"mStatsLock"})
    private final ArrayMap<String, NetworkIdentitySet> mActiveUidIfaces = new ArrayMap<>();
    private final AlarmManager mAlarmManager;
    private INetworkManagementEventObserver mAlertObserver = new BaseNetworkObserver() {
        public void limitReached(String limitName, String iface) {
            NetworkStatsService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", NetworkStatsService.TAG);
            if (NetworkManagementService.LIMIT_GLOBAL_ALERT.equals(limitName) && !NetworkStatsService.this.mHandler.hasMessages(2)) {
                NetworkStatsService.this.mHandler.sendEmptyMessageDelayed(2, 1000);
            }
        }
    };
    private final File mBaseDir;
    private final Clock mClock;
    /* access modifiers changed from: private */
    public final Context mContext;
    @GuardedBy({"mStatsLock"})
    private Network[] mDefaultNetworks = new Network[0];
    @GuardedBy({"mStatsLock"})
    private NetworkStatsRecorder mDevRecorder;
    private long mGlobalAlertBytes;
    /* access modifiers changed from: private */
    public Handler mHandler;
    private Handler.Callback mHandlerCallback;
    private long mLastStatsSessionPoll;
    @GuardedBy({"mStatsLock"})
    private String[] mMobileIfaces = new String[0];
    private final INetworkManagementService mNetworkManager;
    private final DropBoxNonMonotonicObserver mNonMonotonicObserver = new DropBoxNonMonotonicObserver();
    @GuardedBy({"mOpenSessionCallsPerUid"})
    private final SparseIntArray mOpenSessionCallsPerUid = new SparseIntArray();
    private long mPersistThreshold = 2097152;
    private PendingIntent mPollIntent;
    private BroadcastReceiver mPollReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NetworkStatsService.this.performPoll(3);
            NetworkStatsService.this.registerGlobalAlert();
        }
    };
    private BroadcastReceiver mRemovedReceiver = new BroadcastReceiver() {
        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void onReceive(Context context, Intent intent) {
            int uid = intent.getIntExtra("android.intent.extra.UID", -1);
            if (uid != -1) {
                synchronized (NetworkStatsService.this.mStatsLock) {
                    if (NetworkStatsService.this.mSystemReady) {
                        NetworkStatsService.this.mWakeLock.acquire();
                        try {
                            NetworkStatsService.this.removeUidsLocked(uid);
                        } finally {
                            NetworkStatsService.this.mWakeLock.release();
                        }
                    }
                }
            }
        }
    };
    private final NetworkStatsSettings mSettings;
    private BroadcastReceiver mShutdownReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            synchronized (NetworkStatsService.this.mStatsLock) {
                NetworkStatsService.this.shutdownLocked();
            }
        }
    };
    /* access modifiers changed from: private */
    public final Object mStatsLock = new Object();
    private final NetworkStatsObservers mStatsObservers;
    private final File mSystemDir;
    /* access modifiers changed from: private */
    public volatile boolean mSystemReady;
    private final TelephonyManager mTeleManager;
    private BroadcastReceiver mTetherReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            NetworkStatsService.this.performPoll(1);
        }
    };
    private NetworkStats mUidOperations = new NetworkStats(0, 10);
    /* access modifiers changed from: private */
    @GuardedBy({"mStatsLock"})
    public NetworkStatsRecorder mUidRecorder;
    /* access modifiers changed from: private */
    @GuardedBy({"mStatsLock"})
    public NetworkStatsRecorder mUidTagRecorder;
    private final boolean mUseBpfTrafficStats;
    private BroadcastReceiver mUserReceiver = new BroadcastReceiver() {
        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void onReceive(Context context, Intent intent) {
            int userId = intent.getIntExtra("android.intent.extra.user_handle", -1);
            if (userId != -1) {
                synchronized (NetworkStatsService.this.mStatsLock) {
                    if (NetworkStatsService.this.mSystemReady) {
                        NetworkStatsService.this.mWakeLock.acquire();
                        try {
                            NetworkStatsService.this.removeUserLocked(userId);
                        } finally {
                            NetworkStatsService.this.mWakeLock.release();
                        }
                    }
                }
            }
        }
    };
    @GuardedBy({"mStatsLock"})
    private VpnInfo[] mVpnInfos = new VpnInfo[0];
    /* access modifiers changed from: private */
    public final PowerManager.WakeLock mWakeLock;
    @GuardedBy({"mStatsLock"})
    private NetworkStatsRecorder mXtRecorder;
    @GuardedBy({"mStatsLock"})
    private NetworkStatsCollection mXtStatsCached;

    private static native long nativeGetIfaceStat(String str, int i, boolean z);

    private static native long nativeGetTotalStat(int i, boolean z);

    private static native long nativeGetUidStat(int i, int i2, boolean z);

    public interface NetworkStatsSettings {
        boolean getAugmentEnabled();

        Config getDevConfig();

        long getDevPersistBytes(long j);

        long getGlobalAlertBytes(long j);

        long getPollInterval();

        boolean getSampleEnabled();

        Config getUidConfig();

        long getUidPersistBytes(long j);

        Config getUidTagConfig();

        long getUidTagPersistBytes(long j);

        Config getXtConfig();

        long getXtPersistBytes(long j);

        public static class Config {
            public final long bucketDuration;
            public final long deleteAgeMillis;
            public final long rotateAgeMillis;

            public Config(long bucketDuration2, long rotateAgeMillis2, long deleteAgeMillis2) {
                this.bucketDuration = bucketDuration2;
                this.rotateAgeMillis = rotateAgeMillis2;
                this.deleteAgeMillis = deleteAgeMillis2;
            }
        }
    }

    private static File getDefaultSystemDir() {
        return new File(Environment.getDataDirectory(), "system");
    }

    private static File getDefaultBaseDir() {
        File baseDir = new File(getDefaultSystemDir(), "netstats");
        baseDir.mkdirs();
        return baseDir;
    }

    private static Clock getDefaultClock() {
        return new BestClock(ZoneOffset.UTC, new Clock[]{SystemClock.currentNetworkTimeClock(), Clock.systemUTC()});
    }

    private static final class NetworkStatsHandler extends Handler {
        NetworkStatsHandler(Looper looper, Handler.Callback callback) {
            super(looper, callback);
        }
    }

    public static NetworkStatsService create(Context context, INetworkManagementService networkManager) {
        Context context2 = context;
        PowerManager.WakeLock wakeLock = ((PowerManager) context2.getSystemService("power")).newWakeLock(1, TAG);
        NetworkStatsService service = new NetworkStatsService(context, networkManager, (AlarmManager) context2.getSystemService("alarm"), wakeLock, getDefaultClock(), TelephonyManager.getDefault(), new DefaultNetworkStatsSettings(context2), new NetworkStatsObservers(), getDefaultSystemDir(), getDefaultBaseDir());
        service.registerLocalService();
        HandlerThread handlerThread = new HandlerThread(TAG);
        Handler.Callback callback = new HandlerCallback(service);
        handlerThread.start();
        service.setHandler(new NetworkStatsHandler(handlerThread.getLooper(), callback), callback);
        return service;
    }

    @VisibleForTesting
    NetworkStatsService(Context context, INetworkManagementService networkManager, AlarmManager alarmManager, PowerManager.WakeLock wakeLock, Clock clock, TelephonyManager teleManager, NetworkStatsSettings settings, NetworkStatsObservers statsObservers, File systemDir, File baseDir) {
        this.mContext = (Context) Preconditions.checkNotNull(context, "missing Context");
        this.mNetworkManager = (INetworkManagementService) Preconditions.checkNotNull(networkManager, "missing INetworkManagementService");
        this.mAlarmManager = (AlarmManager) Preconditions.checkNotNull(alarmManager, "missing AlarmManager");
        this.mClock = (Clock) Preconditions.checkNotNull(clock, "missing Clock");
        this.mSettings = (NetworkStatsSettings) Preconditions.checkNotNull(settings, "missing NetworkStatsSettings");
        this.mTeleManager = (TelephonyManager) Preconditions.checkNotNull(teleManager, "missing TelephonyManager");
        this.mWakeLock = (PowerManager.WakeLock) Preconditions.checkNotNull(wakeLock, "missing WakeLock");
        this.mStatsObservers = (NetworkStatsObservers) Preconditions.checkNotNull(statsObservers, "missing NetworkStatsObservers");
        this.mSystemDir = (File) Preconditions.checkNotNull(systemDir, "missing systemDir");
        this.mBaseDir = (File) Preconditions.checkNotNull(baseDir, "missing baseDir");
        this.mUseBpfTrafficStats = new File("/sys/fs/bpf/map_netd_app_uid_stats_map").exists();
    }

    private void registerLocalService() {
        LocalServices.addService(NetworkStatsManagerInternal.class, new NetworkStatsManagerInternalImpl());
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setHandler(Handler handler, Handler.Callback callback) {
        this.mHandler = handler;
        this.mHandlerCallback = callback;
    }

    public void systemReady() {
        this.mSystemReady = true;
        if (!isBandwidthControlEnabled()) {
            Slog.w(TAG, "bandwidth controls disabled, unable to track stats");
            return;
        }
        synchronized (this.mStatsLock) {
            this.mDevRecorder = buildRecorder(PREFIX_DEV, this.mSettings.getDevConfig(), false);
            this.mXtRecorder = buildRecorder(PREFIX_XT, this.mSettings.getXtConfig(), false);
            this.mUidRecorder = buildRecorder("uid", this.mSettings.getUidConfig(), false);
            this.mUidTagRecorder = buildRecorder(PREFIX_UID_TAG, this.mSettings.getUidTagConfig(), true);
            updatePersistThresholdsLocked();
            maybeUpgradeLegacyStatsLocked();
            this.mXtStatsCached = this.mXtRecorder.getOrLoadCompleteLocked();
            bootstrapStatsLocked();
        }
        this.mContext.registerReceiver(this.mTetherReceiver, new IntentFilter("android.net.conn.TETHER_STATE_CHANGED"), (String) null, this.mHandler);
        this.mContext.registerReceiver(this.mPollReceiver, new IntentFilter(ACTION_NETWORK_STATS_POLL), "android.permission.READ_NETWORK_USAGE_HISTORY", this.mHandler);
        this.mContext.registerReceiverAsUser(this.mRemovedReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.UID_REMOVED"), (String) null, this.mHandler);
        this.mContext.registerReceiver(this.mUserReceiver, new IntentFilter("android.intent.action.USER_REMOVED"), (String) null, this.mHandler);
        this.mContext.registerReceiver(this.mShutdownReceiver, new IntentFilter("android.intent.action.ACTION_SHUTDOWN"));
        try {
            this.mNetworkManager.registerObserver(this.mAlertObserver);
        } catch (RemoteException e) {
        }
        registerPollAlarmLocked();
        registerGlobalAlert();
    }

    private NetworkStatsRecorder buildRecorder(String prefix, NetworkStatsSettings.Config config, boolean includeTags) {
        return new NetworkStatsRecorder(new FileRotator(this.mBaseDir, prefix, config.rotateAgeMillis, config.deleteAgeMillis), this.mNonMonotonicObserver, (DropBoxManager) this.mContext.getSystemService("dropbox"), prefix, config.bucketDuration, includeTags);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mStatsLock"})
    public void shutdownLocked() {
        this.mContext.unregisterReceiver(this.mTetherReceiver);
        this.mContext.unregisterReceiver(this.mPollReceiver);
        this.mContext.unregisterReceiver(this.mRemovedReceiver);
        this.mContext.unregisterReceiver(this.mUserReceiver);
        this.mContext.unregisterReceiver(this.mShutdownReceiver);
        long currentTime = this.mClock.millis();
        this.mDevRecorder.forcePersistLocked(currentTime);
        this.mXtRecorder.forcePersistLocked(currentTime);
        this.mUidRecorder.forcePersistLocked(currentTime);
        this.mUidTagRecorder.forcePersistLocked(currentTime);
        this.mSystemReady = false;
    }

    @GuardedBy({"mStatsLock"})
    private void maybeUpgradeLegacyStatsLocked() {
        try {
            File file = new File(this.mSystemDir, "netstats.bin");
            if (file.exists()) {
                this.mDevRecorder.importLegacyNetworkLocked(file);
                file.delete();
            }
            File file2 = new File(this.mSystemDir, "netstats_xt.bin");
            if (file2.exists()) {
                file2.delete();
            }
            File file3 = new File(this.mSystemDir, "netstats_uid.bin");
            if (file3.exists()) {
                this.mUidRecorder.importLegacyUidLocked(file3);
                this.mUidTagRecorder.importLegacyUidLocked(file3);
                file3.delete();
            }
        } catch (IOException e) {
            Log.wtf(TAG, "problem during legacy upgrade", e);
        } catch (OutOfMemoryError e2) {
            Log.wtf(TAG, "problem during legacy upgrade", e2);
        }
    }

    private void registerPollAlarmLocked() {
        PendingIntent pendingIntent = this.mPollIntent;
        if (pendingIntent != null) {
            this.mAlarmManager.cancel(pendingIntent);
        }
        this.mPollIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent(ACTION_NETWORK_STATS_POLL), 0);
        this.mAlarmManager.setInexactRepeating(3, SystemClock.elapsedRealtime(), this.mSettings.getPollInterval(), this.mPollIntent);
    }

    /* access modifiers changed from: private */
    public void registerGlobalAlert() {
        try {
            this.mNetworkManager.setGlobalAlert(this.mGlobalAlertBytes);
        } catch (IllegalStateException e) {
            Slog.w(TAG, "problem registering for global alert: " + e);
        } catch (RemoteException e2) {
        }
    }

    public INetworkStatsSession openSession() {
        return openSessionInternal(4, (String) null);
    }

    public INetworkStatsSession openSessionForUsageStats(int flags, String callingPackage) {
        return openSessionInternal(flags, callingPackage);
    }

    private boolean isRateLimitedForPoll(int callingUid) {
        long lastCallTime;
        if (callingUid == 1000) {
            return false;
        }
        long now = SystemClock.elapsedRealtime();
        synchronized (this.mOpenSessionCallsPerUid) {
            this.mOpenSessionCallsPerUid.put(callingUid, this.mOpenSessionCallsPerUid.get(callingUid, 0) + 1);
            lastCallTime = this.mLastStatsSessionPoll;
            this.mLastStatsSessionPoll = now;
        }
        if (now - lastCallTime < POLL_RATE_LIMIT_MS) {
            return true;
        }
        return false;
    }

    private INetworkStatsSession openSessionInternal(int flags, final String callingPackage) {
        final int usedFlags;
        assertBandwidthControlEnabled();
        final int callingUid = Binder.getCallingUid();
        if (isRateLimitedForPoll(callingUid)) {
            usedFlags = flags & -2;
        } else {
            usedFlags = flags;
        }
        if ((usedFlags & 3) != 0) {
            long ident = Binder.clearCallingIdentity();
            try {
                performPoll(3);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
        return new INetworkStatsSession.Stub() {
            private final int mAccessLevel;
            private final String mCallingPackage;
            private final int mCallingUid = callingUid;
            private NetworkStatsCollection mUidComplete;
            private NetworkStatsCollection mUidTagComplete;

            {
                String str = callingPackage;
                this.mCallingPackage = str;
                this.mAccessLevel = NetworkStatsService.this.checkAccessLevel(str);
            }

            private NetworkStatsCollection getUidComplete() {
                NetworkStatsCollection networkStatsCollection;
                synchronized (NetworkStatsService.this.mStatsLock) {
                    if (this.mUidComplete == null) {
                        this.mUidComplete = NetworkStatsService.this.mUidRecorder.getOrLoadCompleteLocked();
                    }
                    networkStatsCollection = this.mUidComplete;
                }
                return networkStatsCollection;
            }

            private NetworkStatsCollection getUidTagComplete() {
                NetworkStatsCollection networkStatsCollection;
                synchronized (NetworkStatsService.this.mStatsLock) {
                    if (this.mUidTagComplete == null) {
                        this.mUidTagComplete = NetworkStatsService.this.mUidTagRecorder.getOrLoadCompleteLocked();
                    }
                    networkStatsCollection = this.mUidTagComplete;
                }
                return networkStatsCollection;
            }

            public int[] getRelevantUids() {
                return getUidComplete().getRelevantUids(this.mAccessLevel);
            }

            public NetworkStats getDeviceSummaryForNetwork(NetworkTemplate template, long start, long end) {
                return NetworkStatsService.this.internalGetSummaryForNetwork(template, usedFlags, start, end, this.mAccessLevel, this.mCallingUid);
            }

            public NetworkStats getSummaryForNetwork(NetworkTemplate template, long start, long end) {
                return NetworkStatsService.this.internalGetSummaryForNetwork(template, usedFlags, start, end, this.mAccessLevel, this.mCallingUid);
            }

            public NetworkStatsHistory getHistoryForNetwork(NetworkTemplate template, int fields) {
                return NetworkStatsService.this.internalGetHistoryForNetwork(template, usedFlags, fields, this.mAccessLevel, this.mCallingUid);
            }

            public NetworkStats getSummaryForAllUid(NetworkTemplate template, long start, long end, boolean includeTags) {
                try {
                    NetworkStats stats = getUidComplete().getSummary(template, start, end, this.mAccessLevel, this.mCallingUid);
                    if (includeTags) {
                        stats.combineAllValues(getUidTagComplete().getSummary(template, start, end, this.mAccessLevel, this.mCallingUid));
                    }
                    return stats;
                } catch (NullPointerException e) {
                    Slog.wtf(NetworkStatsService.TAG, "NullPointerException in getSummaryForAllUid", e);
                    throw e;
                }
            }

            public NetworkStatsHistory getHistoryForUid(NetworkTemplate template, int uid, int set, int tag, int fields) {
                if (tag == 0) {
                    return getUidComplete().getHistory(template, (SubscriptionPlan) null, uid, set, tag, fields, Long.MIN_VALUE, JobStatus.NO_LATEST_RUNTIME, this.mAccessLevel, this.mCallingUid);
                }
                return getUidTagComplete().getHistory(template, (SubscriptionPlan) null, uid, set, tag, fields, Long.MIN_VALUE, JobStatus.NO_LATEST_RUNTIME, this.mAccessLevel, this.mCallingUid);
            }

            public NetworkStatsHistory getHistoryIntervalForUid(NetworkTemplate template, int uid, int set, int tag, int fields, long start, long end) {
                if (tag == 0) {
                    return getUidComplete().getHistory(template, (SubscriptionPlan) null, uid, set, tag, fields, start, end, this.mAccessLevel, this.mCallingUid);
                } else if (uid == Binder.getCallingUid()) {
                    return getUidTagComplete().getHistory(template, (SubscriptionPlan) null, uid, set, tag, fields, start, end, this.mAccessLevel, this.mCallingUid);
                } else {
                    throw new SecurityException("Calling package " + this.mCallingPackage + " cannot access tag information from a different uid");
                }
            }

            public void close() {
                this.mUidComplete = null;
                this.mUidTagComplete = null;
            }
        };
    }

    /* access modifiers changed from: private */
    public int checkAccessLevel(String callingPackage) {
        return NetworkStatsAccess.checkAccessLevel(this.mContext, Binder.getCallingUid(), callingPackage);
    }

    /* JADX INFO: finally extract failed */
    private SubscriptionPlan resolveSubscriptionPlan(NetworkTemplate template, int flags) {
        SubscriptionPlan plan = null;
        if ((flags & 4) != 0 && this.mSettings.getAugmentEnabled()) {
            if (LOGD) {
                Slog.d(TAG, "Resolving plan for " + template);
            }
            long token = Binder.clearCallingIdentity();
            try {
                plan = ((NetworkPolicyManagerInternal) LocalServices.getService(NetworkPolicyManagerInternal.class)).getSubscriptionPlan(template);
                Binder.restoreCallingIdentity(token);
                if (LOGD) {
                    Slog.d(TAG, "Resolved to plan " + plan);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }
        return plan;
    }

    /* access modifiers changed from: private */
    public NetworkStats internalGetSummaryForNetwork(NetworkTemplate template, int flags, long start, long end, int accessLevel, int callingUid) {
        NetworkStatsHistory history = internalGetHistoryForNetwork(template, flags, -1, accessLevel, callingUid);
        NetworkStatsHistory.Entry entry = history.getValues(start, end, System.currentTimeMillis(), (NetworkStatsHistory.Entry) null);
        NetworkStats stats = new NetworkStats(end - start, 1);
        NetworkStatsHistory networkStatsHistory = history;
        NetworkStats.Entry entry2 = r5;
        NetworkStats.Entry entry3 = new NetworkStats.Entry(NetworkStats.IFACE_ALL, -1, -1, 0, -1, -1, -1, entry.rxBytes, entry.rxPackets, entry.txBytes, entry.txPackets, entry.operations);
        stats.addValues(entry2);
        return stats;
    }

    /* access modifiers changed from: private */
    public NetworkStatsHistory internalGetHistoryForNetwork(NetworkTemplate template, int flags, int fields, int accessLevel, int callingUid) {
        Object obj;
        SubscriptionPlan augmentPlan = resolveSubscriptionPlan(template, flags);
        Object obj2 = this.mStatsLock;
        synchronized (obj2) {
            try {
                obj = obj2;
                NetworkStatsHistory history = this.mXtStatsCached.getHistory(template, augmentPlan, -1, -1, 0, fields, Long.MIN_VALUE, JobStatus.NO_LATEST_RUNTIME, accessLevel, callingUid);
                return history;
            } catch (Throwable th) {
                th = th;
                throw th;
            }
        }
    }

    /* access modifiers changed from: private */
    public long getNetworkTotalBytes(NetworkTemplate template, long start, long end) {
        assertSystemReady();
        assertBandwidthControlEnabled();
        return internalGetSummaryForNetwork(template, 4, start, end, 3, Binder.getCallingUid()).getTotalBytes();
    }

    /* access modifiers changed from: private */
    public NetworkStats getNetworkUidBytes(NetworkTemplate template, long start, long end) {
        NetworkStatsCollection uidComplete;
        assertSystemReady();
        assertBandwidthControlEnabled();
        synchronized (this.mStatsLock) {
            uidComplete = this.mUidRecorder.getOrLoadCompleteLocked();
        }
        return uidComplete.getSummary(template, start, end, 3, 1000);
    }

    /* JADX INFO: finally extract failed */
    public NetworkStats getDataLayerSnapshotForUid(int uid) throws RemoteException {
        if (Binder.getCallingUid() != uid) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE", TAG);
        }
        assertBandwidthControlEnabled();
        long token = Binder.clearCallingIdentity();
        try {
            NetworkStats networkLayer = this.mNetworkManager.getNetworkStatsUidDetail(uid, NetworkStats.INTERFACES_ALL);
            Binder.restoreCallingIdentity(token);
            networkLayer.spliceOperationsFrom(this.mUidOperations);
            NetworkStats dataLayer = new NetworkStats(networkLayer.getElapsedRealtime(), networkLayer.size());
            NetworkStats.Entry entry = null;
            for (int i = 0; i < networkLayer.size(); i++) {
                entry = networkLayer.getValues(i, entry);
                entry.iface = NetworkStats.IFACE_ALL;
                dataLayer.combineValues(entry);
            }
            return dataLayer;
        } catch (Throwable networkLayer2) {
            Binder.restoreCallingIdentity(token);
            throw networkLayer2;
        }
    }

    public NetworkStats getDetailedUidStats(String[] requiredIfaces) {
        try {
            return getNetworkStatsUidDetail(NetworkStatsFactory.augmentWithStackedInterfaces(requiredIfaces));
        } catch (RemoteException e) {
            Log.wtf(TAG, "Error compiling UID stats", e);
            return new NetworkStats(0, 0);
        }
    }

    public String[] getMobileIfaces() {
        return this.mMobileIfaces;
    }

    public void incrementOperationCount(int uid, int tag, int operationCount) {
        Object obj;
        int i = uid;
        int i2 = operationCount;
        if (Binder.getCallingUid() != i) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", TAG);
        }
        if (i2 < 0) {
            int i3 = i2;
            throw new IllegalArgumentException("operation count can only be incremented");
        } else if (tag != 0) {
            Object obj2 = this.mStatsLock;
            synchronized (obj2) {
                try {
                    int set = this.mActiveUidCounterSet.get(i, 0);
                    obj = obj2;
                    int i4 = i2;
                    try {
                        this.mUidOperations.combineValues(this.mActiveIface, uid, set, tag, 0, 0, 0, 0, (long) i2);
                        this.mUidOperations.combineValues(this.mActiveIface, uid, set, 0, 0, 0, 0, 0, (long) operationCount);
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    int i5 = i2;
                    obj = obj2;
                    throw th;
                }
            }
        } else {
            int i6 = i2;
            throw new IllegalArgumentException("operation count must have specific tag");
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setUidForeground(int uid, boolean uidForeground) {
        synchronized (this.mStatsLock) {
            int set = uidForeground ? 1 : 0;
            if (this.mActiveUidCounterSet.get(uid, 0) != set) {
                this.mActiveUidCounterSet.put(uid, set);
                NetworkManagementSocketTagger.setKernelCounterSet(uid, set);
            }
        }
    }

    public void forceUpdateIfaces(Network[] defaultNetworks, VpnInfo[] vpnArray, NetworkState[] networkStates, String activeIface) {
        NetworkStack.checkNetworkStackPermission(this.mContext);
        assertBandwidthControlEnabled();
        long token = Binder.clearCallingIdentity();
        try {
            updateIfaces(defaultNetworks, vpnArray, networkStates, activeIface);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public void forceUpdate() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_NETWORK_USAGE_HISTORY", TAG);
        assertBandwidthControlEnabled();
        long token = Binder.clearCallingIdentity();
        try {
            performPoll(3);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* access modifiers changed from: private */
    public void advisePersistThreshold(long thresholdBytes) {
        assertBandwidthControlEnabled();
        this.mPersistThreshold = MathUtils.constrain(thresholdBytes, 131072, 2097152);
        if (LOGV) {
            Slog.v(TAG, "advisePersistThreshold() given " + thresholdBytes + ", clamped to " + this.mPersistThreshold);
        }
        long currentTime = this.mClock.millis();
        synchronized (this.mStatsLock) {
            if (this.mSystemReady) {
                updatePersistThresholdsLocked();
                this.mDevRecorder.maybePersistLocked(currentTime);
                this.mXtRecorder.maybePersistLocked(currentTime);
                this.mUidRecorder.maybePersistLocked(currentTime);
                this.mUidTagRecorder.maybePersistLocked(currentTime);
                registerGlobalAlert();
            }
        }
    }

    /* JADX INFO: finally extract failed */
    public DataUsageRequest registerUsageCallback(String callingPackage, DataUsageRequest request, Messenger messenger, IBinder binder) {
        Preconditions.checkNotNull(callingPackage, "calling package is null");
        Preconditions.checkNotNull(request, "DataUsageRequest is null");
        Preconditions.checkNotNull(request.template, "NetworkTemplate is null");
        Preconditions.checkNotNull(messenger, "messenger is null");
        Preconditions.checkNotNull(binder, "binder is null");
        int callingUid = Binder.getCallingUid();
        int accessLevel = checkAccessLevel(callingPackage);
        long token = Binder.clearCallingIdentity();
        try {
            DataUsageRequest normalizedRequest = this.mStatsObservers.register(request, messenger, binder, callingUid, accessLevel);
            Binder.restoreCallingIdentity(token);
            Handler handler = this.mHandler;
            handler.sendMessage(handler.obtainMessage(1));
            return normalizedRequest;
        } catch (Throwable normalizedRequest2) {
            Binder.restoreCallingIdentity(token);
            throw normalizedRequest2;
        }
    }

    public void unregisterUsageRequest(DataUsageRequest request) {
        Preconditions.checkNotNull(request, "DataUsageRequest is null");
        int callingUid = Binder.getCallingUid();
        long token = Binder.clearCallingIdentity();
        try {
            this.mStatsObservers.unregister(request, callingUid);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public long getUidStats(int uid, int type) {
        return nativeGetUidStat(uid, type, checkBpfStatsEnable());
    }

    public long getIfaceStats(String iface, int type) {
        long nativeIfaceStats = nativeGetIfaceStat(iface, type, checkBpfStatsEnable());
        if (nativeIfaceStats == -1) {
            return nativeIfaceStats;
        }
        return getTetherStats(iface, type) + nativeIfaceStats;
    }

    public long getTotalStats(int type) {
        long nativeTotalStats = nativeGetTotalStat(type, checkBpfStatsEnable());
        if (nativeTotalStats == -1) {
            return nativeTotalStats;
        }
        return getTetherStats(NetworkStats.IFACE_ALL, type) + nativeTotalStats;
    }

    private long getTetherStats(String iface, int type) {
        HashSet<String> limitIfaces;
        long token = Binder.clearCallingIdentity();
        try {
            NetworkStats tetherSnapshot = getNetworkStatsTethering(0);
            Binder.restoreCallingIdentity(token);
            if (iface == NetworkStats.IFACE_ALL) {
                limitIfaces = null;
            } else {
                limitIfaces = new HashSet<>();
                limitIfaces.add(iface);
            }
            NetworkStats.Entry entry = tetherSnapshot.getTotal((NetworkStats.Entry) null, limitIfaces);
            if (LOGD) {
                Slog.d(TAG, "TetherStats: iface=" + iface + " type=" + type + " entry=" + entry);
            }
            if (type == 0) {
                return entry.rxBytes;
            }
            if (type == 1) {
                return entry.rxPackets;
            }
            if (type == 2) {
                return entry.txBytes;
            }
            if (type != 3) {
                return 0;
            }
            return entry.txPackets;
        } catch (RemoteException e) {
            Slog.w(TAG, "Error get TetherStats: " + e);
            Binder.restoreCallingIdentity(token);
            return 0;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    private boolean checkBpfStatsEnable() {
        return this.mUseBpfTrafficStats;
    }

    @GuardedBy({"mStatsLock"})
    private void updatePersistThresholdsLocked() {
        this.mDevRecorder.setPersistThreshold(this.mSettings.getDevPersistBytes(this.mPersistThreshold));
        this.mXtRecorder.setPersistThreshold(this.mSettings.getXtPersistBytes(this.mPersistThreshold));
        this.mUidRecorder.setPersistThreshold(this.mSettings.getUidPersistBytes(this.mPersistThreshold));
        this.mUidTagRecorder.setPersistThreshold(this.mSettings.getUidTagPersistBytes(this.mPersistThreshold));
        this.mGlobalAlertBytes = this.mSettings.getGlobalAlertBytes(this.mPersistThreshold);
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    private void updateIfaces(Network[] defaultNetworks, VpnInfo[] vpnArray, NetworkState[] networkStates, String activeIface) {
        synchronized (this.mStatsLock) {
            this.mWakeLock.acquire();
            try {
                this.mVpnInfos = vpnArray;
                this.mActiveIface = activeIface;
                updateIfacesLocked(defaultNetworks, networkStates);
            } finally {
                this.mWakeLock.release();
            }
        }
    }

    @GuardedBy({"mStatsLock"})
    private void updateIfacesLocked(Network[] defaultNetworks, NetworkState[] states) {
        Network[] networkArr = defaultNetworks;
        NetworkState[] networkStateArr = states;
        if (this.mSystemReady) {
            if (LOGV) {
                Slog.v(TAG, "updateIfacesLocked()");
            }
            performPollLocked(1);
            this.mActiveIfaces.clear();
            this.mActiveUidIfaces.clear();
            if (networkArr != null) {
                this.mDefaultNetworks = networkArr;
            }
            ArraySet<String> mobileIfaces = new ArraySet<>();
            for (NetworkState state : networkStateArr) {
                if (state.networkInfo.isConnected()) {
                    boolean isMobile = ConnectivityManager.isNetworkTypeMobile(state.networkInfo.getType());
                    NetworkIdentity ident = NetworkIdentity.buildNetworkIdentity(this.mContext, state, ArrayUtils.contains(this.mDefaultNetworks, state.network));
                    String baseIface = state.linkProperties.getInterfaceName();
                    if (baseIface != null) {
                        findOrCreateNetworkIdentitySet(this.mActiveIfaces, baseIface).add(ident);
                        findOrCreateNetworkIdentitySet(this.mActiveUidIfaces, baseIface).add(ident);
                        if (state.networkCapabilities.hasCapability(4) && !ident.getMetered()) {
                            NetworkIdentity networkIdentity = new NetworkIdentity(ident.getType(), ident.getSubType(), ident.getSubscriberId(), ident.getNetworkId(), ident.getRoaming(), true, true);
                            findOrCreateNetworkIdentitySet(this.mActiveIfaces, VT_INTERFACE).add(networkIdentity);
                            findOrCreateNetworkIdentitySet(this.mActiveUidIfaces, VT_INTERFACE).add(networkIdentity);
                        }
                        if (isMobile) {
                            mobileIfaces.add(baseIface);
                        }
                    }
                    for (LinkProperties stackedLink : state.linkProperties.getStackedLinks()) {
                        String stackedIface = stackedLink.getInterfaceName();
                        if (stackedIface != null) {
                            if (this.mUseBpfTrafficStats) {
                                findOrCreateNetworkIdentitySet(this.mActiveIfaces, stackedIface).add(ident);
                            }
                            findOrCreateNetworkIdentitySet(this.mActiveUidIfaces, stackedIface).add(ident);
                            if (isMobile) {
                                mobileIfaces.add(stackedIface);
                            }
                            NetworkStatsFactory.noteStackedIface(stackedIface, baseIface);
                        }
                    }
                }
            }
            this.mMobileIfaces = (String[]) mobileIfaces.toArray(new String[mobileIfaces.size()]);
        }
    }

    private static <K> NetworkIdentitySet findOrCreateNetworkIdentitySet(ArrayMap<K, NetworkIdentitySet> map, K key) {
        NetworkIdentitySet ident = map.get(key);
        if (ident != null) {
            return ident;
        }
        NetworkIdentitySet ident2 = new NetworkIdentitySet();
        map.put(key, ident2);
        return ident2;
    }

    @GuardedBy({"mStatsLock"})
    private void recordSnapshotLocked(long currentTime) throws RemoteException {
        Trace.traceBegin(2097152, "snapshotUid");
        NetworkStats uidSnapshot = getNetworkStatsUidDetail(NetworkStats.INTERFACES_ALL);
        Trace.traceEnd(2097152);
        Trace.traceBegin(2097152, "snapshotXt");
        NetworkStats xtSnapshot = getNetworkStatsXt();
        Trace.traceEnd(2097152);
        Trace.traceBegin(2097152, "snapshotDev");
        NetworkStats devSnapshot = this.mNetworkManager.getNetworkStatsSummaryDev();
        Trace.traceEnd(2097152);
        Trace.traceBegin(2097152, "snapshotTether");
        NetworkStats tetherSnapshot = getNetworkStatsTethering(0);
        Trace.traceEnd(2097152);
        xtSnapshot.combineAllValues(tetherSnapshot);
        devSnapshot.combineAllValues(tetherSnapshot);
        Trace.traceBegin(2097152, "recordDev");
        long j = currentTime;
        this.mDevRecorder.recordSnapshotLocked(devSnapshot, this.mActiveIfaces, (VpnInfo[]) null, j);
        Trace.traceEnd(2097152);
        Trace.traceBegin(2097152, "recordXt");
        this.mXtRecorder.recordSnapshotLocked(xtSnapshot, this.mActiveIfaces, (VpnInfo[]) null, j);
        Trace.traceEnd(2097152);
        VpnInfo[] vpnArray = this.mVpnInfos;
        Trace.traceBegin(2097152, "recordUid");
        NetworkStats networkStats = uidSnapshot;
        VpnInfo[] vpnInfoArr = vpnArray;
        long j2 = currentTime;
        this.mUidRecorder.recordSnapshotLocked(networkStats, this.mActiveUidIfaces, vpnInfoArr, j2);
        Trace.traceEnd(2097152);
        Trace.traceBegin(2097152, "recordUidTag");
        this.mUidTagRecorder.recordSnapshotLocked(networkStats, this.mActiveUidIfaces, vpnInfoArr, j2);
        Trace.traceEnd(2097152);
        this.mStatsObservers.updateStats(xtSnapshot, uidSnapshot, new ArrayMap(this.mActiveIfaces), new ArrayMap(this.mActiveUidIfaces), vpnArray, currentTime);
    }

    @GuardedBy({"mStatsLock"})
    private void bootstrapStatsLocked() {
        try {
            recordSnapshotLocked(this.mClock.millis());
        } catch (IllegalStateException e) {
            Slog.w(TAG, "problem reading network stats: " + e);
        } catch (RemoteException e2) {
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* access modifiers changed from: private */
    public void performPoll(int flags) {
        synchronized (this.mStatsLock) {
            this.mWakeLock.acquire();
            try {
                performPollLocked(flags);
            } finally {
                this.mWakeLock.release();
            }
        }
    }

    @GuardedBy({"mStatsLock"})
    private void performPollLocked(int flags) {
        if (this.mSystemReady) {
            if (LOGV) {
                Slog.v(TAG, "performPollLocked(flags=0x" + Integer.toHexString(flags) + ")");
            }
            Trace.traceBegin(2097152, "performPollLocked");
            boolean persistForce = false;
            boolean persistNetwork = (flags & 1) != 0;
            boolean persistUid = (flags & 2) != 0;
            if ((flags & 256) != 0) {
                persistForce = true;
            }
            long currentTime = this.mClock.millis();
            try {
                recordSnapshotLocked(currentTime);
                Trace.traceBegin(2097152, "[persisting]");
                if (persistForce) {
                    this.mDevRecorder.forcePersistLocked(currentTime);
                    this.mXtRecorder.forcePersistLocked(currentTime);
                    this.mUidRecorder.forcePersistLocked(currentTime);
                    this.mUidTagRecorder.forcePersistLocked(currentTime);
                } else {
                    if (persistNetwork) {
                        this.mDevRecorder.maybePersistLocked(currentTime);
                        this.mXtRecorder.maybePersistLocked(currentTime);
                    }
                    if (persistUid) {
                        this.mUidRecorder.maybePersistLocked(currentTime);
                        this.mUidTagRecorder.maybePersistLocked(currentTime);
                    }
                }
                Trace.traceEnd(2097152);
                if (this.mSettings.getSampleEnabled()) {
                    performSampleLocked();
                }
                Intent updatedIntent = new Intent(ACTION_NETWORK_STATS_UPDATED);
                updatedIntent.setFlags(1073741824);
                this.mContext.sendBroadcastAsUser(updatedIntent, UserHandle.ALL, "android.permission.READ_NETWORK_USAGE_HISTORY");
                Trace.traceEnd(2097152);
            } catch (IllegalStateException e) {
                Log.wtf(TAG, "problem reading network stats", e);
            } catch (RemoteException e2) {
            }
        }
    }

    @GuardedBy({"mStatsLock"})
    private void performSampleLocked() {
        long currentTime = this.mClock.millis();
        NetworkTemplate template = NetworkTemplate.buildTemplateMobileWildcard();
        NetworkStats.Entry devTotal = this.mDevRecorder.getTotalSinceBootLocked(template);
        NetworkStats.Entry xtTotal = this.mXtRecorder.getTotalSinceBootLocked(template);
        NetworkStats.Entry uidTotal = this.mUidRecorder.getTotalSinceBootLocked(template);
        long j = devTotal.rxBytes;
        NetworkStats.Entry uidTotal2 = uidTotal;
        NetworkTemplate networkTemplate = template;
        NetworkStats.Entry uidTotal3 = uidTotal2;
        NetworkStats.Entry entry = devTotal;
        long j2 = j;
        EventLogTags.writeNetstatsMobileSample(j2, devTotal.rxPackets, devTotal.txBytes, devTotal.txPackets, xtTotal.rxBytes, xtTotal.rxPackets, xtTotal.txBytes, xtTotal.txPackets, uidTotal3.rxBytes, uidTotal3.rxPackets, uidTotal3.txBytes, uidTotal3.txPackets, currentTime);
        NetworkTemplate template2 = NetworkTemplate.buildTemplateWifiWildcard();
        NetworkStats.Entry devTotal2 = this.mDevRecorder.getTotalSinceBootLocked(template2);
        NetworkStats.Entry xtTotal2 = this.mXtRecorder.getTotalSinceBootLocked(template2);
        NetworkStats.Entry uidTotal4 = this.mUidRecorder.getTotalSinceBootLocked(template2);
        EventLogTags.writeNetstatsWifiSample(devTotal2.rxBytes, devTotal2.rxPackets, devTotal2.txBytes, devTotal2.txPackets, xtTotal2.rxBytes, xtTotal2.rxPackets, xtTotal2.txBytes, xtTotal2.txPackets, uidTotal4.rxBytes, uidTotal4.rxPackets, uidTotal4.txBytes, uidTotal4.txPackets, currentTime);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mStatsLock"})
    public void removeUidsLocked(int... uids) {
        if (LOGV) {
            Slog.v(TAG, "removeUidsLocked() for UIDs " + Arrays.toString(uids));
        }
        performPollLocked(3);
        this.mUidRecorder.removeUidsLocked(uids);
        this.mUidTagRecorder.removeUidsLocked(uids);
        for (int uid : uids) {
            NetworkManagementSocketTagger.resetKernelUidStats(uid);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mStatsLock"})
    public void removeUserLocked(int userId) {
        if (LOGV) {
            Slog.v(TAG, "removeUserLocked() for userId=" + userId);
        }
        int[] uids = new int[0];
        for (ApplicationInfo app : this.mContext.getPackageManager().getInstalledApplications(4194816)) {
            uids = ArrayUtils.appendInt(uids, UserHandle.getUid(userId, app.uid));
        }
        removeUidsLocked(uids);
    }

    private class NetworkStatsManagerInternalImpl extends NetworkStatsManagerInternal {
        private NetworkStatsManagerInternalImpl() {
        }

        public long getNetworkTotalBytes(NetworkTemplate template, long start, long end) {
            Trace.traceBegin(2097152, "getNetworkTotalBytes");
            try {
                return NetworkStatsService.this.getNetworkTotalBytes(template, start, end);
            } finally {
                Trace.traceEnd(2097152);
            }
        }

        public NetworkStats getNetworkUidBytes(NetworkTemplate template, long start, long end) {
            Trace.traceBegin(2097152, "getNetworkUidBytes");
            try {
                return NetworkStatsService.this.getNetworkUidBytes(template, start, end);
            } finally {
                Trace.traceEnd(2097152);
            }
        }

        public void setUidForeground(int uid, boolean uidForeground) {
            NetworkStatsService.this.setUidForeground(uid, uidForeground);
        }

        public void advisePersistThreshold(long thresholdBytes) {
            NetworkStatsService.this.advisePersistThreshold(thresholdBytes);
        }

        public void forceUpdate() {
            NetworkStatsService.this.forceUpdate();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 23 */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x024e, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dump(java.io.FileDescriptor r24, java.io.PrintWriter r25, java.lang.String[] r26) {
        /*
            r23 = this;
            r1 = r23
            r8 = r25
            r9 = r26
            android.content.Context r0 = r1.mContext
            java.lang.String r2 = "NetworkStats"
            boolean r0 = com.android.internal.util.DumpUtils.checkDumpPermission(r0, r2, r8)
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            r2 = 86400000(0x5265c00, double:4.2687272E-316)
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r10 = r0
            int r4 = r9.length
            r5 = 0
            r11 = r2
            r2 = r5
        L_0x001e:
            if (r2 >= r4) goto L_0x003d
            r3 = r9[r2]
            r10.add(r3)
            java.lang.String r0 = "--duration="
            boolean r0 = r3.startsWith(r0)
            if (r0 == 0) goto L_0x003a
            r0 = 11
            java.lang.String r0 = r3.substring(r0)     // Catch:{ NumberFormatException -> 0x0039 }
            long r6 = java.lang.Long.parseLong(r0)     // Catch:{ NumberFormatException -> 0x0039 }
            r11 = r6
            goto L_0x003a
        L_0x0039:
            r0 = move-exception
        L_0x003a:
            int r2 = r2 + 1
            goto L_0x001e
        L_0x003d:
            java.lang.String r0 = "--poll"
            boolean r0 = r10.contains(r0)
            r2 = 1
            if (r0 != 0) goto L_0x0052
            java.lang.String r0 = "poll"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x0050
            goto L_0x0052
        L_0x0050:
            r0 = r5
            goto L_0x0053
        L_0x0052:
            r0 = r2
        L_0x0053:
            r13 = r0
            java.lang.String r0 = "--checkin"
            boolean r14 = r10.contains(r0)
            java.lang.String r0 = "--full"
            boolean r0 = r10.contains(r0)
            if (r0 != 0) goto L_0x006d
            java.lang.String r0 = "full"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x006b
            goto L_0x006d
        L_0x006b:
            r0 = r5
            goto L_0x006e
        L_0x006d:
            r0 = r2
        L_0x006e:
            r15 = r0
            java.lang.String r0 = "--uid"
            boolean r0 = r10.contains(r0)
            if (r0 != 0) goto L_0x0082
            java.lang.String r0 = "detail"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x0080
            goto L_0x0082
        L_0x0080:
            r0 = r5
            goto L_0x0083
        L_0x0082:
            r0 = r2
        L_0x0083:
            r16 = r0
            java.lang.String r0 = "--tag"
            boolean r0 = r10.contains(r0)
            if (r0 != 0) goto L_0x0097
            java.lang.String r0 = "detail"
            boolean r0 = r10.contains(r0)
            if (r0 == 0) goto L_0x0096
            goto L_0x0097
        L_0x0096:
            r2 = r5
        L_0x0097:
            r17 = r2
            com.android.internal.util.IndentingPrintWriter r0 = new com.android.internal.util.IndentingPrintWriter
            java.lang.String r2 = "  "
            r0.<init>(r8, r2)
            r6 = r0
            java.lang.Object r7 = r1.mStatsLock
            monitor-enter(r7)
            int r0 = r9.length     // Catch:{ all -> 0x0254 }
            if (r0 <= 0) goto L_0x00be
            java.lang.String r0 = "--proto"
            r2 = r9[r5]     // Catch:{ all -> 0x00b6 }
            boolean r0 = r0.equals(r2)     // Catch:{ all -> 0x00b6 }
            if (r0 == 0) goto L_0x00be
            r23.dumpProtoLocked(r24)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r7)     // Catch:{ all -> 0x00b6 }
            return
        L_0x00b6:
            r0 = move-exception
            r8 = r6
            r22 = r7
            r18 = r10
            goto L_0x025a
        L_0x00be:
            if (r13 == 0) goto L_0x00cc
            r0 = 259(0x103, float:3.63E-43)
            r1.performPollLocked(r0)     // Catch:{ all -> 0x00b6 }
            java.lang.String r0 = "Forced poll"
            r6.println(r0)     // Catch:{ all -> 0x00b6 }
            monitor-exit(r7)     // Catch:{ all -> 0x00b6 }
            return
        L_0x00cc:
            if (r14 == 0) goto L_0x0134
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x012c }
            r18 = r2
            long r20 = r18 - r11
            java.lang.String r0 = "v1,"
            r6.print(r0)     // Catch:{ all -> 0x012c }
            r2 = 1000(0x3e8, double:4.94E-321)
            long r4 = r20 / r2
            r6.print(r4)     // Catch:{ all -> 0x012c }
            r0 = 44
            r6.print(r0)     // Catch:{ all -> 0x012c }
            long r2 = r18 / r2
            r6.print(r2)     // Catch:{ all -> 0x012c }
            r6.println()     // Catch:{ all -> 0x012c }
            java.lang.String r0 = "xt"
            r6.println(r0)     // Catch:{ all -> 0x012c }
            com.android.server.net.NetworkStatsRecorder r2 = r1.mXtRecorder     // Catch:{ all -> 0x012c }
            r3 = r25
            r4 = r20
            r8 = r6
            r22 = r7
            r6 = r18
            r2.dumpCheckin(r3, r4, r6)     // Catch:{ all -> 0x0166 }
            if (r16 == 0) goto L_0x0117
            java.lang.String r0 = "uid"
            r8.println(r0)     // Catch:{ all -> 0x0166 }
            com.android.server.net.NetworkStatsRecorder r2 = r1.mUidRecorder     // Catch:{ all -> 0x0166 }
            r3 = r25
            r4 = r20
            r6 = r18
            r2.dumpCheckin(r3, r4, r6)     // Catch:{ all -> 0x0166 }
        L_0x0117:
            if (r17 == 0) goto L_0x012a
            java.lang.String r0 = "tag"
            r8.println(r0)     // Catch:{ all -> 0x0166 }
            com.android.server.net.NetworkStatsRecorder r2 = r1.mUidTagRecorder     // Catch:{ all -> 0x0166 }
            r3 = r25
            r4 = r20
            r6 = r18
            r2.dumpCheckin(r3, r4, r6)     // Catch:{ all -> 0x0166 }
        L_0x012a:
            monitor-exit(r22)     // Catch:{ all -> 0x0166 }
            return
        L_0x012c:
            r0 = move-exception
            r8 = r6
            r22 = r7
            r18 = r10
            goto L_0x025a
        L_0x0134:
            r8 = r6
            r22 = r7
            java.lang.String r0 = "Active interfaces:"
            r8.println(r0)     // Catch:{ all -> 0x0250 }
            r8.increaseIndent()     // Catch:{ all -> 0x0250 }
            r0 = r5
        L_0x0140:
            android.util.ArrayMap<java.lang.String, com.android.server.net.NetworkIdentitySet> r2 = r1.mActiveIfaces     // Catch:{ all -> 0x0250 }
            int r2 = r2.size()     // Catch:{ all -> 0x0250 }
            if (r0 >= r2) goto L_0x016b
            java.lang.String r2 = "iface"
            android.util.ArrayMap<java.lang.String, com.android.server.net.NetworkIdentitySet> r3 = r1.mActiveIfaces     // Catch:{ all -> 0x0166 }
            java.lang.Object r3 = r3.keyAt(r0)     // Catch:{ all -> 0x0166 }
            r8.printPair(r2, r3)     // Catch:{ all -> 0x0166 }
            java.lang.String r2 = "ident"
            android.util.ArrayMap<java.lang.String, com.android.server.net.NetworkIdentitySet> r3 = r1.mActiveIfaces     // Catch:{ all -> 0x0166 }
            java.lang.Object r3 = r3.valueAt(r0)     // Catch:{ all -> 0x0166 }
            r8.printPair(r2, r3)     // Catch:{ all -> 0x0166 }
            r8.println()     // Catch:{ all -> 0x0166 }
            int r0 = r0 + 1
            goto L_0x0140
        L_0x0166:
            r0 = move-exception
            r18 = r10
            goto L_0x025a
        L_0x016b:
            r8.decreaseIndent()     // Catch:{ all -> 0x0250 }
            java.lang.String r0 = "Active UID interfaces:"
            r8.println(r0)     // Catch:{ all -> 0x0250 }
            r8.increaseIndent()     // Catch:{ all -> 0x0250 }
            r0 = r5
        L_0x0177:
            android.util.ArrayMap<java.lang.String, com.android.server.net.NetworkIdentitySet> r2 = r1.mActiveUidIfaces     // Catch:{ all -> 0x0250 }
            int r2 = r2.size()     // Catch:{ all -> 0x0250 }
            if (r0 >= r2) goto L_0x019d
            java.lang.String r2 = "iface"
            android.util.ArrayMap<java.lang.String, com.android.server.net.NetworkIdentitySet> r3 = r1.mActiveUidIfaces     // Catch:{ all -> 0x0166 }
            java.lang.Object r3 = r3.keyAt(r0)     // Catch:{ all -> 0x0166 }
            r8.printPair(r2, r3)     // Catch:{ all -> 0x0166 }
            java.lang.String r2 = "ident"
            android.util.ArrayMap<java.lang.String, com.android.server.net.NetworkIdentitySet> r3 = r1.mActiveUidIfaces     // Catch:{ all -> 0x0166 }
            java.lang.Object r3 = r3.valueAt(r0)     // Catch:{ all -> 0x0166 }
            r8.printPair(r2, r3)     // Catch:{ all -> 0x0166 }
            r8.println()     // Catch:{ all -> 0x0166 }
            int r0 = r0 + 1
            goto L_0x0177
        L_0x019d:
            r8.decreaseIndent()     // Catch:{ all -> 0x0250 }
            android.util.SparseIntArray r2 = r1.mOpenSessionCallsPerUid     // Catch:{ all -> 0x0250 }
            monitor-enter(r2)     // Catch:{ all -> 0x0250 }
            android.util.SparseIntArray r0 = r1.mOpenSessionCallsPerUid     // Catch:{ all -> 0x0249 }
            android.util.SparseIntArray r0 = r0.clone()     // Catch:{ all -> 0x0249 }
            monitor-exit(r2)     // Catch:{ all -> 0x0249 }
            int r2 = r0.size()     // Catch:{ all -> 0x0250 }
            long[] r3 = new long[r2]     // Catch:{ all -> 0x0250 }
            r4 = 0
        L_0x01b1:
            r6 = 32
            if (r4 >= r2) goto L_0x01cd
            int r7 = r0.valueAt(r4)     // Catch:{ all -> 0x0250 }
            r18 = r10
            long r9 = (long) r7
            long r6 = r9 << r6
            int r9 = r0.keyAt(r4)     // Catch:{ all -> 0x025c }
            long r9 = (long) r9     // Catch:{ all -> 0x025c }
            long r6 = r6 | r9
            r3[r4] = r6     // Catch:{ all -> 0x025c }
            int r4 = r4 + 1
            r9 = r26
            r10 = r18
            goto L_0x01b1
        L_0x01cd:
            r18 = r10
            java.util.Arrays.sort(r3)     // Catch:{ all -> 0x025c }
            java.lang.String r4 = "Top openSession callers (uid=count):"
            r8.println(r4)     // Catch:{ all -> 0x025c }
            r8.increaseIndent()     // Catch:{ all -> 0x025c }
            int r4 = r2 + -20
            int r4 = java.lang.Math.max(r5, r4)     // Catch:{ all -> 0x025c }
            int r5 = r2 + -1
        L_0x01e2:
            if (r5 < r4) goto L_0x01fd
            r9 = r3[r5]     // Catch:{ all -> 0x025c }
            r19 = -1
            long r9 = r9 & r19
            int r7 = (int) r9     // Catch:{ all -> 0x025c }
            r9 = r3[r5]     // Catch:{ all -> 0x025c }
            long r9 = r9 >> r6
            int r9 = (int) r9     // Catch:{ all -> 0x025c }
            r8.print(r7)     // Catch:{ all -> 0x025c }
            java.lang.String r10 = "="
            r8.print(r10)     // Catch:{ all -> 0x025c }
            r8.println(r9)     // Catch:{ all -> 0x025c }
            int r5 = r5 + -1
            goto L_0x01e2
        L_0x01fd:
            r8.decreaseIndent()     // Catch:{ all -> 0x025c }
            r8.println()     // Catch:{ all -> 0x025c }
            java.lang.String r5 = "Dev stats:"
            r8.println(r5)     // Catch:{ all -> 0x025c }
            r8.increaseIndent()     // Catch:{ all -> 0x025c }
            com.android.server.net.NetworkStatsRecorder r5 = r1.mDevRecorder     // Catch:{ all -> 0x025c }
            r5.dumpLocked(r8, r15)     // Catch:{ all -> 0x025c }
            r8.decreaseIndent()     // Catch:{ all -> 0x025c }
            java.lang.String r5 = "Xt stats:"
            r8.println(r5)     // Catch:{ all -> 0x025c }
            r8.increaseIndent()     // Catch:{ all -> 0x025c }
            com.android.server.net.NetworkStatsRecorder r5 = r1.mXtRecorder     // Catch:{ all -> 0x025c }
            r5.dumpLocked(r8, r15)     // Catch:{ all -> 0x025c }
            r8.decreaseIndent()     // Catch:{ all -> 0x025c }
            if (r16 == 0) goto L_0x0235
            java.lang.String r5 = "UID stats:"
            r8.println(r5)     // Catch:{ all -> 0x025c }
            r8.increaseIndent()     // Catch:{ all -> 0x025c }
            com.android.server.net.NetworkStatsRecorder r5 = r1.mUidRecorder     // Catch:{ all -> 0x025c }
            r5.dumpLocked(r8, r15)     // Catch:{ all -> 0x025c }
            r8.decreaseIndent()     // Catch:{ all -> 0x025c }
        L_0x0235:
            if (r17 == 0) goto L_0x0247
            java.lang.String r5 = "UID tag stats:"
            r8.println(r5)     // Catch:{ all -> 0x025c }
            r8.increaseIndent()     // Catch:{ all -> 0x025c }
            com.android.server.net.NetworkStatsRecorder r5 = r1.mUidTagRecorder     // Catch:{ all -> 0x025c }
            r5.dumpLocked(r8, r15)     // Catch:{ all -> 0x025c }
            r8.decreaseIndent()     // Catch:{ all -> 0x025c }
        L_0x0247:
            monitor-exit(r22)     // Catch:{ all -> 0x025c }
            return
        L_0x0249:
            r0 = move-exception
            r18 = r10
        L_0x024c:
            monitor-exit(r2)     // Catch:{ all -> 0x024e }
            throw r0     // Catch:{ all -> 0x025c }
        L_0x024e:
            r0 = move-exception
            goto L_0x024c
        L_0x0250:
            r0 = move-exception
            r18 = r10
            goto L_0x025a
        L_0x0254:
            r0 = move-exception
            r8 = r6
            r22 = r7
            r18 = r10
        L_0x025a:
            monitor-exit(r22)     // Catch:{ all -> 0x025c }
            throw r0
        L_0x025c:
            r0 = move-exception
            goto L_0x025a
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.NetworkStatsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    @GuardedBy({"mStatsLock"})
    private void dumpProtoLocked(FileDescriptor fd) {
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        dumpInterfaces(proto, 2246267895809L, this.mActiveIfaces);
        dumpInterfaces(proto, 2246267895810L, this.mActiveUidIfaces);
        this.mDevRecorder.writeToProtoLocked(proto, 1146756268035L);
        this.mXtRecorder.writeToProtoLocked(proto, 1146756268036L);
        this.mUidRecorder.writeToProtoLocked(proto, 1146756268037L);
        this.mUidTagRecorder.writeToProtoLocked(proto, 1146756268038L);
        proto.flush();
    }

    private static void dumpInterfaces(ProtoOutputStream proto, long tag, ArrayMap<String, NetworkIdentitySet> ifaces) {
        for (int i = 0; i < ifaces.size(); i++) {
            long start = proto.start(tag);
            proto.write(1138166333441L, ifaces.keyAt(i));
            ifaces.valueAt(i).writeToProto(proto, 1146756268034L);
            proto.end(start);
        }
    }

    private NetworkStats getNetworkStatsUidDetail(String[] ifaces) throws RemoteException {
        NetworkStats uidSnapshot = this.mNetworkManager.getNetworkStatsUidDetail(-1, ifaces);
        NetworkStats tetherSnapshot = getNetworkStatsTethering(1);
        tetherSnapshot.filter(-1, ifaces, -1);
        NetworkStatsFactory.apply464xlatAdjustments(uidSnapshot, tetherSnapshot, this.mUseBpfTrafficStats);
        uidSnapshot.combineAllValues(tetherSnapshot);
        NetworkStats vtStats = ((TelephonyManager) this.mContext.getSystemService("phone")).getVtDataUsage(1);
        if (vtStats != null) {
            vtStats.filter(-1, ifaces, -1);
            NetworkStatsFactory.apply464xlatAdjustments(uidSnapshot, vtStats, this.mUseBpfTrafficStats);
            uidSnapshot.combineAllValues(vtStats);
        }
        uidSnapshot.combineAllValues(this.mUidOperations);
        return uidSnapshot;
    }

    private NetworkStats getNetworkStatsXt() throws RemoteException {
        NetworkStats xtSnapshot = this.mNetworkManager.getNetworkStatsSummaryXt();
        NetworkStats vtSnapshot = ((TelephonyManager) this.mContext.getSystemService("phone")).getVtDataUsage(0);
        if (vtSnapshot != null) {
            xtSnapshot.combineAllValues(vtSnapshot);
        }
        return xtSnapshot;
    }

    private NetworkStats getNetworkStatsTethering(int how) throws RemoteException {
        try {
            return this.mNetworkManager.getNetworkStatsTethering(how);
        } catch (IllegalStateException e) {
            Log.wtf(TAG, "problem reading network stats", e);
            return new NetworkStats(0, 10);
        }
    }

    @VisibleForTesting
    static class HandlerCallback implements Handler.Callback {
        private final NetworkStatsService mService;

        HandlerCallback(NetworkStatsService service) {
            this.mService = service;
        }

        public boolean handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                this.mService.performPoll(3);
                return true;
            } else if (i != 2) {
                return false;
            } else {
                this.mService.performPoll(1);
                this.mService.registerGlobalAlert();
                return true;
            }
        }
    }

    private void assertSystemReady() {
        if (!this.mSystemReady) {
            throw new IllegalStateException("System not ready");
        }
    }

    private void assertBandwidthControlEnabled() {
        if (!isBandwidthControlEnabled()) {
            throw new IllegalStateException("Bandwidth module disabled");
        }
    }

    private boolean isBandwidthControlEnabled() {
        long token = Binder.clearCallingIdentity();
        try {
            return this.mNetworkManager.isBandwidthControlEnabled();
        } catch (RemoteException e) {
            return false;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private class DropBoxNonMonotonicObserver implements NetworkStats.NonMonotonicObserver<String> {
        private DropBoxNonMonotonicObserver() {
        }

        public void foundNonMonotonic(NetworkStats left, int leftIndex, NetworkStats right, int rightIndex, String cookie) {
            Log.w(NetworkStatsService.TAG, "Found non-monotonic values; saving to dropbox");
            StringBuilder builder = new StringBuilder();
            builder.append("found non-monotonic " + cookie + " values at left[" + leftIndex + "] - right[" + rightIndex + "]\n");
            builder.append("left=");
            builder.append(left);
            builder.append(10);
            builder.append("right=");
            builder.append(right);
            builder.append(10);
            ((DropBoxManager) NetworkStatsService.this.mContext.getSystemService(DropBoxManager.class)).addText(NetworkStatsService.TAG_NETSTATS_ERROR, builder.toString());
        }

        public void foundNonMonotonic(NetworkStats stats, int statsIndex, String cookie) {
            Log.w(NetworkStatsService.TAG, "Found non-monotonic values; saving to dropbox");
            StringBuilder builder = new StringBuilder();
            builder.append("Found non-monotonic " + cookie + " values at [" + statsIndex + "]\n");
            builder.append("stats=");
            builder.append(stats);
            builder.append(10);
            ((DropBoxManager) NetworkStatsService.this.mContext.getSystemService(DropBoxManager.class)).addText(NetworkStatsService.TAG_NETSTATS_ERROR, builder.toString());
        }
    }

    private static class DefaultNetworkStatsSettings implements NetworkStatsSettings {
        private final ContentResolver mResolver;

        public DefaultNetworkStatsSettings(Context context) {
            this.mResolver = (ContentResolver) Preconditions.checkNotNull(context.getContentResolver());
        }

        private long getGlobalLong(String name, long def) {
            return Settings.Global.getLong(this.mResolver, name, def);
        }

        private boolean getGlobalBoolean(String name, boolean def) {
            return Settings.Global.getInt(this.mResolver, name, (int) def) != 0;
        }

        public long getPollInterval() {
            return getGlobalLong("netstats_poll_interval", 1800000);
        }

        public long getGlobalAlertBytes(long def) {
            return getGlobalLong("netstats_global_alert_bytes", def);
        }

        public boolean getSampleEnabled() {
            return getGlobalBoolean("netstats_sample_enabled", true);
        }

        public boolean getAugmentEnabled() {
            return getGlobalBoolean("netstats_augment_enabled", true);
        }

        public NetworkStatsSettings.Config getDevConfig() {
            return new NetworkStatsSettings.Config(getGlobalLong("netstats_dev_bucket_duration", 3600000), getGlobalLong("netstats_dev_rotate_age", 1296000000), getGlobalLong("netstats_dev_delete_age", 7776000000L));
        }

        public NetworkStatsSettings.Config getXtConfig() {
            return getDevConfig();
        }

        public NetworkStatsSettings.Config getUidConfig() {
            return new NetworkStatsSettings.Config(getGlobalLong("netstats_uid_bucket_duration", 7200000), getGlobalLong("netstats_uid_rotate_age", 1296000000), getGlobalLong("netstats_uid_delete_age", 7776000000L));
        }

        public NetworkStatsSettings.Config getUidTagConfig() {
            return new NetworkStatsSettings.Config(getGlobalLong("netstats_uid_tag_bucket_duration", 7200000), getGlobalLong("netstats_uid_tag_rotate_age", 432000000), getGlobalLong("netstats_uid_tag_delete_age", 1296000000));
        }

        public long getDevPersistBytes(long def) {
            return getGlobalLong("netstats_dev_persist_bytes", def);
        }

        public long getXtPersistBytes(long def) {
            return getDevPersistBytes(def);
        }

        public long getUidPersistBytes(long def) {
            return getGlobalLong("netstats_uid_persist_bytes", def);
        }

        public long getUidTagPersistBytes(long def) {
            return getGlobalLong("netstats_uid_tag_persist_bytes", def);
        }
    }
}
