package com.android.server.content;

import android.accounts.Account;
import android.accounts.AccountAndUser;
import android.accounts.AccountManager;
import android.app.backup.BackupManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ISyncStatusObserver;
import android.content.PeriodicSync;
import android.content.SyncInfo;
import android.content.SyncRequest;
import android.content.SyncStatusInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Log;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.IntPair;
import com.android.server.content.MiSyncConstants;
import com.android.server.content.SyncManager;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.voiceinteraction.DatabaseHelper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

public class SyncStorageEngine {
    private static final int ACCOUNTS_VERSION = 3;
    private static final double DEFAULT_FLEX_PERCENT_SYNC = 0.04d;
    private static final long DEFAULT_MIN_FLEX_ALLOWED_SECS = 5;
    private static final long DEFAULT_POLL_FREQUENCY_SECONDS = 86400;
    public static final int EVENT_START = 0;
    public static final int EVENT_STOP = 1;
    public static final int MAX_HISTORY = 100;
    public static final String MESG_CANCELED = "canceled";
    public static final String MESG_SUCCESS = "success";
    @VisibleForTesting
    static final long MILLIS_IN_4WEEKS = 2419200000L;
    private static final int MSG_WRITE_STATISTICS = 2;
    private static final int MSG_WRITE_STATUS = 1;
    public static final long NOT_IN_BACKOFF_MODE = -1;
    public static final String[] SOURCES = {"OTHER", "LOCAL", "POLL", "USER", "PERIODIC", "FEED"};
    public static final int SOURCE_FEED = 5;
    public static final int SOURCE_LOCAL = 1;
    public static final int SOURCE_OTHER = 0;
    public static final int SOURCE_PERIODIC = 4;
    public static final int SOURCE_POLL = 2;
    public static final int SOURCE_USER = 3;
    public static final int STATISTICS_FILE_END = 0;
    public static final int STATISTICS_FILE_ITEM = 101;
    public static final int STATISTICS_FILE_ITEM_OLD = 100;
    public static final int STATUS_FILE_END = 0;
    public static final int STATUS_FILE_ITEM = 100;
    private static final boolean SYNC_ENABLED_DEFAULT = false;
    private static final String TAG = "SyncManager";
    private static final String TAG_FILE = "SyncManagerFile";
    private static final long WRITE_STATISTICS_DELAY = 1800000;
    private static final long WRITE_STATUS_DELAY = 600000;
    private static final String XML_ATTR_ENABLED = "enabled";
    private static final String XML_ATTR_LISTEN_FOR_TICKLES = "listen-for-tickles";
    private static final String XML_ATTR_NEXT_AUTHORITY_ID = "nextAuthorityId";
    private static final String XML_ATTR_SYNC_RANDOM_OFFSET = "offsetInSeconds";
    private static final String XML_ATTR_USER = "user";
    private static final String XML_TAG_LISTEN_FOR_TICKLES = "listenForTickles";
    /* access modifiers changed from: private */
    public static PeriodicSyncAddedListener mPeriodicSyncAddedListener;
    private static HashMap<String, String> sAuthorityRenames = new HashMap<>();
    private static volatile SyncStorageEngine sSyncStorageEngine = null;
    private final AtomicFile mAccountInfoFile;
    private final HashMap<AccountAndUser, AccountInfo> mAccounts = new HashMap<>();
    /* access modifiers changed from: private */
    public final SparseArray<AuthorityInfo> mAuthorities = new SparseArray<>();
    private OnAuthorityRemovedListener mAuthorityRemovedListener;
    private final Calendar mCal;
    private final RemoteCallbackList<ISyncStatusObserver> mChangeListeners = new RemoteCallbackList<>();
    private final Context mContext;
    private final SparseArray<ArrayList<SyncInfo>> mCurrentSyncs = new SparseArray<>();
    private final DayStats[] mDayStats = new DayStats[28];
    private boolean mDefaultMasterSyncAutomatically;
    private boolean mGrantSyncAdaptersAccountAccess;
    private final MyHandler mHandler;
    private volatile boolean mIsClockValid;
    private final SyncLogger mLogger;
    private SparseArray<Boolean> mMasterSyncAutomatically = new SparseArray<>();
    private int mNextAuthorityId = 0;
    private int mNextHistoryId = 0;
    private final ArrayMap<ComponentName, SparseArray<AuthorityInfo>> mServices = new ArrayMap<>();
    private final AtomicFile mStatisticsFile;
    private final AtomicFile mStatusFile;
    private final ArrayList<SyncHistoryItem> mSyncHistory = new ArrayList<>();
    private int mSyncRandomOffset;
    private OnSyncRequestListener mSyncRequestListener;
    private final SparseArray<SyncStatusInfo> mSyncStatus = new SparseArray<>();
    private int mYear;
    private int mYearInDays;

    interface OnAuthorityRemovedListener {
        void onAuthorityRemoved(EndPoint endPoint);
    }

    interface OnSyncRequestListener {
        void onSyncRequest(EndPoint endPoint, int i, Bundle bundle, int i2, int i3, int i4);
    }

    interface PeriodicSyncAddedListener {
        void onPeriodicSyncAdded(EndPoint endPoint, Bundle bundle, long j, long j2);
    }

    public static class SyncHistoryItem {
        int authorityId;
        long downstreamActivity;
        long elapsedTime;
        int event;
        long eventTime;
        Bundle extras;
        int historyId;
        boolean initialization;
        String mesg;
        int reason;
        int source;
        int syncExemptionFlag;
        long upstreamActivity;
    }

    static {
        sAuthorityRenames.put("contacts", "com.android.contacts");
        sAuthorityRenames.put("calendar", "com.android.calendar");
    }

    static class AccountInfo {
        final AccountAndUser accountAndUser;
        final HashMap<String, AuthorityInfo> authorities = new HashMap<>();

        AccountInfo(AccountAndUser accountAndUser2) {
            this.accountAndUser = accountAndUser2;
        }
    }

    public static class EndPoint {
        public static final EndPoint USER_ALL_PROVIDER_ALL_ACCOUNTS_ALL = new EndPoint((Account) null, (String) null, -1);
        final Account account;
        final String provider;
        final int userId;

        public EndPoint(Account account2, String provider2, int userId2) {
            this.account = account2;
            this.provider = provider2;
            this.userId = userId2;
        }

        public boolean matchesSpec(EndPoint spec) {
            boolean accountsMatch;
            boolean providersMatch;
            int i = this.userId;
            int i2 = spec.userId;
            if (i != i2 && i != -1 && i2 != -1) {
                return false;
            }
            Account account2 = spec.account;
            if (account2 == null) {
                accountsMatch = true;
            } else {
                accountsMatch = this.account.equals(account2);
            }
            String str = spec.provider;
            if (str == null) {
                providersMatch = true;
            } else {
                providersMatch = this.provider.equals(str);
            }
            if (!accountsMatch || !providersMatch) {
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Account account2 = this.account;
            sb.append(account2 == null ? "ALL ACCS" : account2.name);
            sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
            String str = this.provider;
            if (str == null) {
                str = "ALL PDRS";
            }
            sb.append(str);
            sb.append(":u" + this.userId);
            return sb.toString();
        }

        public String toSafeString() {
            StringBuilder sb = new StringBuilder();
            Account account2 = this.account;
            sb.append(account2 == null ? "ALL ACCS" : SyncLogger.logSafe(account2));
            sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
            String str = this.provider;
            if (str == null) {
                str = "ALL PDRS";
            }
            sb.append(str);
            sb.append(":u" + this.userId);
            return sb.toString();
        }
    }

    public static class AuthorityInfo {
        public static final int NOT_INITIALIZED = -1;
        public static final int NOT_SYNCABLE = 0;
        public static final int SYNCABLE = 1;
        public static final int SYNCABLE_NOT_INITIALIZED = 2;
        public static final int SYNCABLE_NO_ACCOUNT_ACCESS = 3;
        public static final int UNDEFINED = -2;
        long backoffDelay;
        long backoffTime;
        long delayUntil;
        boolean enabled;
        final int ident;
        final ArrayList<PeriodicSync> periodicSyncs;
        int syncable;
        final EndPoint target;

        AuthorityInfo(AuthorityInfo toCopy) {
            this.target = toCopy.target;
            this.ident = toCopy.ident;
            this.enabled = toCopy.enabled;
            this.syncable = toCopy.syncable;
            this.backoffTime = toCopy.backoffTime;
            this.backoffDelay = toCopy.backoffDelay;
            this.delayUntil = toCopy.delayUntil;
            this.periodicSyncs = new ArrayList<>();
            Iterator<PeriodicSync> it = toCopy.periodicSyncs.iterator();
            while (it.hasNext()) {
                this.periodicSyncs.add(new PeriodicSync(it.next()));
            }
        }

        AuthorityInfo(EndPoint info, int id) {
            this.target = info;
            this.ident = id;
            this.enabled = false;
            this.periodicSyncs = new ArrayList<>();
            defaultInitialisation();
        }

        private void defaultInitialisation() {
            this.syncable = -1;
            this.backoffTime = -1;
            this.backoffDelay = -1;
            EndPoint endPoint = this.target;
            if (((endPoint == null || endPoint.account == null || MiSyncConstants.Config.XIAOMI_ACCOUNT_TYPE.equals(this.target.account.type)) ? false : true) && SyncStorageEngine.mPeriodicSyncAddedListener != null) {
                SyncStorageEngine.mPeriodicSyncAddedListener.onPeriodicSyncAdded(this.target, new Bundle(), SyncStorageEngine.DEFAULT_POLL_FREQUENCY_SECONDS, SyncStorageEngine.calculateDefaultFlexTime(SyncStorageEngine.DEFAULT_POLL_FREQUENCY_SECONDS));
            }
        }

        public String toString() {
            return this.target + ", enabled=" + this.enabled + ", syncable=" + this.syncable + ", backoff=" + this.backoffTime + ", delay=" + this.delayUntil;
        }
    }

    public static class DayStats {
        public final int day;
        public int failureCount;
        public long failureTime;
        public int successCount;
        public long successTime;

        public DayStats(int day2) {
            this.day = day2;
        }
    }

    private static class AccountAuthorityValidator {
        private final AccountManager mAccountManager;
        private final SparseArray<Account[]> mAccountsCache = new SparseArray<>();
        private final PackageManager mPackageManager;
        private final SparseArray<ArrayMap<String, Boolean>> mProvidersPerUserCache = new SparseArray<>();

        AccountAuthorityValidator(Context context) {
            this.mAccountManager = (AccountManager) context.getSystemService(AccountManager.class);
            this.mPackageManager = context.getPackageManager();
        }

        /* access modifiers changed from: package-private */
        public boolean isAccountValid(Account account, int userId) {
            Account[] accountsForUser = this.mAccountsCache.get(userId);
            if (accountsForUser == null) {
                accountsForUser = this.mAccountManager.getAccountsAsUser(userId);
                this.mAccountsCache.put(userId, accountsForUser);
            }
            return ArrayUtils.contains(accountsForUser, account);
        }

        /* access modifiers changed from: package-private */
        public boolean isAuthorityValid(String authority, int userId) {
            ArrayMap<String, Boolean> authorityMap = this.mProvidersPerUserCache.get(userId);
            if (authorityMap == null) {
                authorityMap = new ArrayMap<>();
                this.mProvidersPerUserCache.put(userId, authorityMap);
            }
            if (!authorityMap.containsKey(authority)) {
                authorityMap.put(authority, Boolean.valueOf(this.mPackageManager.resolveContentProviderAsUser(authority, 786432, userId) != null));
            }
            return authorityMap.get(authority).booleanValue();
        }
    }

    private SyncStorageEngine(Context context, File dataDir, Looper looper) {
        this.mHandler = new MyHandler(looper);
        this.mContext = context;
        sSyncStorageEngine = this;
        this.mLogger = SyncLogger.getInstance();
        this.mCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"));
        this.mDefaultMasterSyncAutomatically = this.mContext.getResources().getBoolean(17891554);
        File syncDir = new File(new File(dataDir, "system"), "sync");
        syncDir.mkdirs();
        maybeDeleteLegacyPendingInfoLocked(syncDir);
        this.mAccountInfoFile = new AtomicFile(new File(syncDir, "accounts.xml"), "sync-accounts");
        this.mStatusFile = new AtomicFile(new File(syncDir, "status.bin"), "sync-status");
        this.mStatisticsFile = new AtomicFile(new File(syncDir, "stats.bin"), "sync-stats");
        readAccountInfoLocked();
        readStatusLocked();
        readStatisticsLocked();
        SyncStorageEngineInjector.initAndReadAndWriteLocked(syncDir);
        if (this.mLogger.enabled()) {
            int size = this.mAuthorities.size();
            this.mLogger.log("Loaded ", Integer.valueOf(size), " items");
            for (int i = 0; i < size; i++) {
                this.mLogger.log(this.mAuthorities.valueAt(i));
            }
        }
    }

    public static SyncStorageEngine newTestInstance(Context context) {
        return new SyncStorageEngine(context, context.getFilesDir(), Looper.getMainLooper());
    }

    public static void init(Context context, Looper looper) {
        if (sSyncStorageEngine == null) {
            sSyncStorageEngine = new SyncStorageEngine(context, Environment.getDataDirectory(), looper);
        }
    }

    public static SyncStorageEngine getSingleton() {
        if (sSyncStorageEngine != null) {
            return sSyncStorageEngine;
        }
        throw new IllegalStateException("not initialized");
    }

    /* access modifiers changed from: protected */
    public void setOnSyncRequestListener(OnSyncRequestListener listener) {
        if (this.mSyncRequestListener == null) {
            this.mSyncRequestListener = listener;
        }
    }

    /* access modifiers changed from: protected */
    public void setOnAuthorityRemovedListener(OnAuthorityRemovedListener listener) {
        if (this.mAuthorityRemovedListener == null) {
            this.mAuthorityRemovedListener = listener;
        }
    }

    /* access modifiers changed from: protected */
    public void setPeriodicSyncAddedListener(PeriodicSyncAddedListener listener) {
        if (mPeriodicSyncAddedListener == null) {
            mPeriodicSyncAddedListener = listener;
        }
    }

    private class MyHandler extends Handler {
        public MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                synchronized (SyncStorageEngine.this.mAuthorities) {
                    SyncStorageEngine.this.writeStatusLocked();
                }
            } else if (msg.what == 2) {
                synchronized (SyncStorageEngine.this.mAuthorities) {
                    SyncStorageEngine.this.writeStatisticsLocked();
                }
            }
        }
    }

    public int getSyncRandomOffset() {
        return this.mSyncRandomOffset;
    }

    public void addStatusChangeListener(int mask, int userId, ISyncStatusObserver callback) {
        synchronized (this.mAuthorities) {
            this.mChangeListeners.register(callback, Long.valueOf(IntPair.of(userId, mask)));
        }
    }

    public void removeStatusChangeListener(ISyncStatusObserver callback) {
        synchronized (this.mAuthorities) {
            this.mChangeListeners.unregister(callback);
        }
    }

    public static long calculateDefaultFlexTime(long syncTimeSeconds) {
        if (syncTimeSeconds < DEFAULT_MIN_FLEX_ALLOWED_SECS) {
            return 0;
        }
        if (syncTimeSeconds < DEFAULT_POLL_FREQUENCY_SECONDS) {
            return (long) (((double) syncTimeSeconds) * DEFAULT_FLEX_PERCENT_SYNC);
        }
        return 3456;
    }

    /* access modifiers changed from: package-private */
    public void reportChange(int which, int callingUserId) {
        ArrayList<ISyncStatusObserver> reports = null;
        synchronized (this.mAuthorities) {
            int i = this.mChangeListeners.beginBroadcast();
            while (i > 0) {
                i--;
                long cookie = ((Long) this.mChangeListeners.getBroadcastCookie(i)).longValue();
                int userId = IntPair.first(cookie);
                if ((which & IntPair.second(cookie)) != 0) {
                    if (callingUserId == userId) {
                        if (reports == null) {
                            reports = new ArrayList<>(i);
                        }
                        reports.add(this.mChangeListeners.getBroadcastItem(i));
                    }
                }
            }
            this.mChangeListeners.finishBroadcast();
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "reportChange " + which + " to: " + reports);
        }
        if (reports != null) {
            int i2 = reports.size();
            while (i2 > 0) {
                i2--;
                try {
                    reports.get(i2).onStatusChanged(which);
                } catch (RemoteException e) {
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001b, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean getSyncAutomatically(android.accounts.Account r8, int r9, java.lang.String r10) {
        /*
            r7 = this;
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r0 = r7.mAuthorities
            monitor-enter(r0)
            r1 = 1
            r2 = 0
            if (r8 == 0) goto L_0x001c
            com.android.server.content.SyncStorageEngine$EndPoint r3 = new com.android.server.content.SyncStorageEngine$EndPoint     // Catch:{ all -> 0x0044 }
            r3.<init>(r8, r10, r9)     // Catch:{ all -> 0x0044 }
            java.lang.String r4 = "getSyncAutomatically"
            com.android.server.content.SyncStorageEngine$AuthorityInfo r3 = r7.getAuthorityLocked(r3, r4)     // Catch:{ all -> 0x0044 }
            if (r3 == 0) goto L_0x0019
            boolean r4 = r3.enabled     // Catch:{ all -> 0x0044 }
            if (r4 == 0) goto L_0x0019
            goto L_0x001a
        L_0x0019:
            r1 = r2
        L_0x001a:
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            return r1
        L_0x001c:
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r3 = r7.mAuthorities     // Catch:{ all -> 0x0044 }
            int r3 = r3.size()     // Catch:{ all -> 0x0044 }
        L_0x0022:
            if (r3 <= 0) goto L_0x0042
            int r3 = r3 + -1
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r4 = r7.mAuthorities     // Catch:{ all -> 0x0044 }
            java.lang.Object r4 = r4.valueAt(r3)     // Catch:{ all -> 0x0044 }
            com.android.server.content.SyncStorageEngine$AuthorityInfo r4 = (com.android.server.content.SyncStorageEngine.AuthorityInfo) r4     // Catch:{ all -> 0x0044 }
            com.android.server.content.SyncStorageEngine$EndPoint r5 = r4.target     // Catch:{ all -> 0x0044 }
            com.android.server.content.SyncStorageEngine$EndPoint r6 = new com.android.server.content.SyncStorageEngine$EndPoint     // Catch:{ all -> 0x0044 }
            r6.<init>(r8, r10, r9)     // Catch:{ all -> 0x0044 }
            boolean r5 = r5.matchesSpec(r6)     // Catch:{ all -> 0x0044 }
            if (r5 == 0) goto L_0x0041
            boolean r5 = r4.enabled     // Catch:{ all -> 0x0044 }
            if (r5 == 0) goto L_0x0041
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            return r1
        L_0x0041:
            goto L_0x0022
        L_0x0042:
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            return r2
        L_0x0044:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0044 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.getSyncAutomatically(android.accounts.Account, int, java.lang.String):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x00bc, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x00cb, code lost:
        if (r14 == false) goto L_0x00e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x00cd, code lost:
        requestSync(r17, r18, -6, r19, new android.os.Bundle(), r21, r22, r23);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x00e4, code lost:
        reportChange(1, r12);
        queueBackup();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x00ea, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setSyncAutomatically(android.accounts.Account r17, int r18, java.lang.String r19, boolean r20, int r21, int r22, int r23) {
        /*
            r16 = this;
            r10 = r16
            r11 = r17
            r12 = r18
            r13 = r19
            r14 = r20
            r0 = 2
            java.lang.String r1 = "SyncManager"
            boolean r1 = android.util.Log.isLoggable(r1, r0)
            if (r1 == 0) goto L_0x003a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "setSyncAutomatically:  provider "
            r1.append(r2)
            r1.append(r13)
            java.lang.String r2 = ", user "
            r1.append(r2)
            r1.append(r12)
            java.lang.String r2 = " -> "
            r1.append(r2)
            r1.append(r14)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "SyncManager"
            android.util.Slog.d(r2, r1)
        L_0x003a:
            com.android.server.content.SyncLogger r1 = r10.mLogger
            r2 = 12
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            java.lang.String r4 = "Set sync auto account="
            r2[r3] = r4
            r15 = 1
            r2[r15] = r11
            java.lang.String r4 = " user="
            r2[r0] = r4
            r4 = 3
            java.lang.Integer r5 = java.lang.Integer.valueOf(r18)
            r2[r4] = r5
            r4 = 4
            java.lang.String r5 = " authority="
            r2[r4] = r5
            r4 = 5
            r2[r4] = r13
            r4 = 6
            java.lang.String r5 = " value="
            r2[r4] = r5
            r4 = 7
            java.lang.String r5 = java.lang.Boolean.toString(r20)
            r2[r4] = r5
            r4 = 8
            java.lang.String r5 = " cuid="
            r2[r4] = r5
            r4 = 9
            java.lang.Integer r5 = java.lang.Integer.valueOf(r22)
            r2[r4] = r5
            r4 = 10
            java.lang.String r5 = " cpid="
            r2[r4] = r5
            r4 = 11
            java.lang.Integer r5 = java.lang.Integer.valueOf(r23)
            r2[r4] = r5
            r1.log(r2)
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r1 = r10.mAuthorities
            monitor-enter(r1)
            com.android.server.content.SyncStorageEngine$EndPoint r2 = new com.android.server.content.SyncStorageEngine$EndPoint     // Catch:{ all -> 0x00eb }
            r2.<init>(r11, r13, r12)     // Catch:{ all -> 0x00eb }
            r4 = -1
            com.android.server.content.SyncStorageEngine$AuthorityInfo r2 = r10.getOrCreateAuthorityLocked(r2, r4, r3)     // Catch:{ all -> 0x00eb }
            boolean r3 = r2.enabled     // Catch:{ all -> 0x00eb }
            if (r3 != r14) goto L_0x00bd
            java.lang.String r3 = "SyncManager"
            boolean r0 = android.util.Log.isLoggable(r3, r0)     // Catch:{ all -> 0x00eb }
            if (r0 == 0) goto L_0x00bb
            java.lang.String r0 = "SyncManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00eb }
            r3.<init>()     // Catch:{ all -> 0x00eb }
            java.lang.String r4 = "setSyncAutomatically: already set to "
            r3.append(r4)     // Catch:{ all -> 0x00eb }
            r3.append(r14)     // Catch:{ all -> 0x00eb }
            java.lang.String r4 = ", doing nothing"
            r3.append(r4)     // Catch:{ all -> 0x00eb }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00eb }
            android.util.Slog.d(r0, r3)     // Catch:{ all -> 0x00eb }
        L_0x00bb:
            monitor-exit(r1)     // Catch:{ all -> 0x00eb }
            return
        L_0x00bd:
            if (r14 == 0) goto L_0x00c5
            int r3 = r2.syncable     // Catch:{ all -> 0x00eb }
            if (r3 != r0) goto L_0x00c5
            r2.syncable = r4     // Catch:{ all -> 0x00eb }
        L_0x00c5:
            r2.enabled = r14     // Catch:{ all -> 0x00eb }
            r16.writeAccountInfoLocked()     // Catch:{ all -> 0x00eb }
            monitor-exit(r1)     // Catch:{ all -> 0x00eb }
            if (r14 == 0) goto L_0x00e4
            r4 = -6
            android.os.Bundle r6 = new android.os.Bundle
            r6.<init>()
            r1 = r16
            r2 = r17
            r3 = r18
            r5 = r19
            r7 = r21
            r8 = r22
            r9 = r23
            r1.requestSync(r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x00e4:
            r10.reportChange(r15, r12)
            r16.queueBackup()
            return
        L_0x00eb:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x00eb }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.setSyncAutomatically(android.accounts.Account, int, java.lang.String, boolean, int, int, int):void");
    }

    public int getIsSyncable(Account account, int userId, String providerName) {
        synchronized (this.mAuthorities) {
            if (account != null) {
                AuthorityInfo authority = getAuthorityLocked(new EndPoint(account, providerName, userId), "get authority syncable");
                if (authority == null) {
                    return -1;
                }
                int i = authority.syncable;
                return i;
            }
            int i2 = this.mAuthorities.size();
            while (i2 > 0) {
                i2--;
                AuthorityInfo authorityInfo = this.mAuthorities.valueAt(i2);
                if (authorityInfo.target != null && authorityInfo.target.provider.equals(providerName)) {
                    int i3 = authorityInfo.syncable;
                    return i3;
                }
            }
            return -1;
        }
    }

    public void setIsSyncable(Account account, int userId, String providerName, int syncable, int callingUid, int callingPid) {
        setSyncableStateForEndPoint(new EndPoint(account, providerName, userId), syncable, callingUid, callingPid);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x009b, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a2, code lost:
        if (r12 != 1) goto L_0x00b4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00a4, code lost:
        requestSync(r11, -5, new android.os.Bundle(), 0, r16, r17);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00b4, code lost:
        reportChange(1, r9.userId);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00b9, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setSyncableStateForEndPoint(com.android.server.content.SyncStorageEngine.EndPoint r14, int r15, int r16, int r17) {
        /*
            r13 = this;
            r8 = r13
            r9 = r14
            com.android.server.content.SyncLogger r0 = r8.mLogger
            r1 = 8
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            java.lang.String r3 = "Set syncable "
            r1[r2] = r3
            r10 = 1
            r1[r10] = r9
            r3 = 2
            java.lang.String r4 = " value="
            r1[r3] = r4
            java.lang.String r4 = java.lang.Integer.toString(r15)
            r5 = 3
            r1[r5] = r4
            r4 = 4
            java.lang.String r5 = " cuid="
            r1[r4] = r5
            java.lang.Integer r4 = java.lang.Integer.valueOf(r16)
            r5 = 5
            r1[r5] = r4
            r4 = 6
            java.lang.String r5 = " cpid="
            r1[r4] = r5
            java.lang.Integer r4 = java.lang.Integer.valueOf(r17)
            r5 = 7
            r1[r5] = r4
            r0.log(r1)
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r1 = r8.mAuthorities
            monitor-enter(r1)
            r0 = -1
            com.android.server.content.SyncStorageEngine$AuthorityInfo r2 = r13.getOrCreateAuthorityLocked(r14, r0, r2)     // Catch:{ all -> 0x00ba }
            r11 = r2
            r2 = r15
            if (r2 >= r0) goto L_0x0046
            r0 = -1
            r12 = r0
            goto L_0x0047
        L_0x0046:
            r12 = r2
        L_0x0047:
            java.lang.String r0 = "SyncManager"
            boolean r0 = android.util.Log.isLoggable(r0, r3)     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x0072
            java.lang.String r0 = "SyncManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bf }
            r2.<init>()     // Catch:{ all -> 0x00bf }
            java.lang.String r4 = "setIsSyncable: "
            r2.append(r4)     // Catch:{ all -> 0x00bf }
            java.lang.String r4 = r11.toString()     // Catch:{ all -> 0x00bf }
            r2.append(r4)     // Catch:{ all -> 0x00bf }
            java.lang.String r4 = " -> "
            r2.append(r4)     // Catch:{ all -> 0x00bf }
            r2.append(r12)     // Catch:{ all -> 0x00bf }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00bf }
            android.util.Slog.d(r0, r2)     // Catch:{ all -> 0x00bf }
        L_0x0072:
            int r0 = r11.syncable     // Catch:{ all -> 0x00bf }
            if (r0 != r12) goto L_0x009c
            java.lang.String r0 = "SyncManager"
            boolean r0 = android.util.Log.isLoggable(r0, r3)     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x009a
            java.lang.String r0 = "SyncManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bf }
            r2.<init>()     // Catch:{ all -> 0x00bf }
            java.lang.String r3 = "setIsSyncable: already set to "
            r2.append(r3)     // Catch:{ all -> 0x00bf }
            r2.append(r12)     // Catch:{ all -> 0x00bf }
            java.lang.String r3 = ", doing nothing"
            r2.append(r3)     // Catch:{ all -> 0x00bf }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00bf }
            android.util.Slog.d(r0, r2)     // Catch:{ all -> 0x00bf }
        L_0x009a:
            monitor-exit(r1)     // Catch:{ all -> 0x00bf }
            return
        L_0x009c:
            r11.syncable = r12     // Catch:{ all -> 0x00bf }
            r13.writeAccountInfoLocked()     // Catch:{ all -> 0x00bf }
            monitor-exit(r1)     // Catch:{ all -> 0x00bf }
            if (r12 != r10) goto L_0x00b4
            r3 = -5
            android.os.Bundle r4 = new android.os.Bundle
            r4.<init>()
            r5 = 0
            r1 = r13
            r2 = r11
            r6 = r16
            r7 = r17
            r1.requestSync(r2, r3, r4, r5, r6, r7)
        L_0x00b4:
            int r0 = r9.userId
            r13.reportChange(r10, r0)
            return
        L_0x00ba:
            r0 = move-exception
            r2 = r15
            r12 = r2
        L_0x00bd:
            monitor-exit(r1)     // Catch:{ all -> 0x00bf }
            throw r0
        L_0x00bf:
            r0 = move-exception
            goto L_0x00bd
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.setSyncableStateForEndPoint(com.android.server.content.SyncStorageEngine$EndPoint, int, int, int):void");
    }

    public Pair<Long, Long> getBackoff(EndPoint info) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getAuthorityLocked(info, "getBackoff");
            if (authority == null) {
                return null;
            }
            Pair<Long, Long> create = Pair.create(Long.valueOf(authority.backoffTime), Long.valueOf(authority.backoffDelay));
            return create;
        }
    }

    public void setBackoff(EndPoint info, long nextSyncTime, long nextDelay) {
        boolean changed;
        int i;
        EndPoint endPoint = info;
        long j = nextSyncTime;
        long j2 = nextDelay;
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setBackoff: " + endPoint + " -> nextSyncTime " + j + ", nextDelay " + j2);
        }
        synchronized (this.mAuthorities) {
            if (endPoint.account != null) {
                if (endPoint.provider != null) {
                    AuthorityInfo authorityInfo = getOrCreateAuthorityLocked(endPoint, -1, true);
                    if (authorityInfo.backoffTime == j && authorityInfo.backoffDelay == j2) {
                        changed = false;
                        i = 1;
                    } else {
                        authorityInfo.backoffTime = j;
                        authorityInfo.backoffDelay = j2;
                        changed = true;
                        i = 1;
                    }
                }
            }
            i = 1;
            changed = setBackoffLocked(endPoint.account, endPoint.userId, endPoint.provider, nextSyncTime, nextDelay);
        }
        if (changed) {
            reportChange(i, endPoint.userId);
        }
    }

    private boolean setBackoffLocked(Account account, int userId, String providerName, long nextSyncTime, long nextDelay) {
        boolean changed = false;
        for (AccountInfo accountInfo : this.mAccounts.values()) {
            if (account == null || account.equals(accountInfo.accountAndUser.account) || userId == accountInfo.accountAndUser.userId) {
                for (AuthorityInfo authorityInfo : accountInfo.authorities.values()) {
                    if ((providerName == null || providerName.equals(authorityInfo.target.provider)) && !(authorityInfo.backoffTime == nextSyncTime && authorityInfo.backoffDelay == nextDelay)) {
                        authorityInfo.backoffTime = nextSyncTime;
                        authorityInfo.backoffDelay = nextDelay;
                        changed = true;
                    }
                }
            }
        }
        return changed;
    }

    public void clearAllBackoffsLocked() {
        ArraySet<Integer> changedUserIds = new ArraySet<>();
        synchronized (this.mAuthorities) {
            for (AccountInfo accountInfo : this.mAccounts.values()) {
                for (AuthorityInfo authorityInfo : accountInfo.authorities.values()) {
                    if (authorityInfo.backoffTime != -1 || authorityInfo.backoffDelay != -1) {
                        if (Log.isLoggable("SyncManager", 2)) {
                            Slog.v("SyncManager", "clearAllBackoffsLocked: authority:" + authorityInfo.target + " account:*** user:" + accountInfo.accountAndUser.userId + " backoffTime was: " + authorityInfo.backoffTime + " backoffDelay was: " + authorityInfo.backoffDelay);
                        }
                        authorityInfo.backoffTime = -1;
                        authorityInfo.backoffDelay = -1;
                        changedUserIds.add(Integer.valueOf(accountInfo.accountAndUser.userId));
                    }
                }
            }
        }
        for (int i = changedUserIds.size() - 1; i > 0; i--) {
            reportChange(1, changedUserIds.valueAt(i).intValue());
        }
    }

    public long getDelayUntilTime(EndPoint info) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getAuthorityLocked(info, "getDelayUntil");
            if (authority == null) {
                return 0;
            }
            long j = authority.delayUntil;
            return j;
        }
    }

    public void setDelayUntilTime(EndPoint info, long delayUntil) {
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "setDelayUntil: " + info + " -> delayUntil " + delayUntil);
        }
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getOrCreateAuthorityLocked(info, -1, true);
            if (authority.delayUntil != delayUntil) {
                authority.delayUntil = delayUntil;
                reportChange(1, info.userId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean restoreAllPeriodicSyncs() {
        if (mPeriodicSyncAddedListener == null) {
            return false;
        }
        synchronized (this.mAuthorities) {
            for (int i = 0; i < this.mAuthorities.size(); i++) {
                AuthorityInfo authority = this.mAuthorities.valueAt(i);
                Iterator<PeriodicSync> it = authority.periodicSyncs.iterator();
                while (it.hasNext()) {
                    PeriodicSync periodicSync = it.next();
                    mPeriodicSyncAddedListener.onPeriodicSyncAdded(authority.target, periodicSync.extras, periodicSync.period, periodicSync.flexTime);
                }
                authority.periodicSyncs.clear();
            }
            writeAccountInfoLocked();
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0061, code lost:
        if (r14 == false) goto L_0x0076;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0063, code lost:
        requestSync((android.accounts.Account) null, r15, -7, (java.lang.String) null, new android.os.Bundle(), r16, r17, r18);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0076, code lost:
        reportChange(1, r15);
        r10.mContext.sendBroadcast(android.content.ContentResolver.ACTION_SYNC_CONN_STATUS_CHANGED);
        queueBackup();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0083, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMasterSyncAutomatically(boolean r14, int r15, int r16, int r17, int r18) {
        /*
            r13 = this;
            r10 = r13
            r11 = r15
            com.android.server.content.SyncLogger r0 = r10.mLogger
            r1 = 8
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            java.lang.String r3 = "Set master enabled="
            r1[r2] = r3
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r14)
            r12 = 1
            r1[r12] = r2
            r2 = 2
            java.lang.String r3 = " user="
            r1[r2] = r3
            java.lang.Integer r2 = java.lang.Integer.valueOf(r15)
            r3 = 3
            r1[r3] = r2
            r2 = 4
            java.lang.String r3 = " cuid="
            r1[r2] = r3
            java.lang.Integer r2 = java.lang.Integer.valueOf(r17)
            r3 = 5
            r1[r3] = r2
            r2 = 6
            java.lang.String r3 = " cpid="
            r1[r2] = r3
            java.lang.Integer r2 = java.lang.Integer.valueOf(r18)
            r3 = 7
            r1[r3] = r2
            r0.log(r1)
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r1 = r10.mAuthorities
            monitor-enter(r1)
            android.util.SparseArray<java.lang.Boolean> r0 = r10.mMasterSyncAutomatically     // Catch:{ all -> 0x0084 }
            java.lang.Object r0 = r0.get(r15)     // Catch:{ all -> 0x0084 }
            java.lang.Boolean r0 = (java.lang.Boolean) r0     // Catch:{ all -> 0x0084 }
            if (r0 == 0) goto L_0x0054
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r14)     // Catch:{ all -> 0x0084 }
            boolean r2 = r0.equals(r2)     // Catch:{ all -> 0x0084 }
            if (r2 == 0) goto L_0x0054
            monitor-exit(r1)     // Catch:{ all -> 0x0084 }
            return
        L_0x0054:
            android.util.SparseArray<java.lang.Boolean> r2 = r10.mMasterSyncAutomatically     // Catch:{ all -> 0x0084 }
            java.lang.Boolean r3 = java.lang.Boolean.valueOf(r14)     // Catch:{ all -> 0x0084 }
            r2.put(r15, r3)     // Catch:{ all -> 0x0084 }
            r13.writeAccountInfoLocked()     // Catch:{ all -> 0x0084 }
            monitor-exit(r1)     // Catch:{ all -> 0x0084 }
            if (r14 == 0) goto L_0x0076
            r2 = 0
            r4 = -7
            r5 = 0
            android.os.Bundle r6 = new android.os.Bundle
            r6.<init>()
            r1 = r13
            r3 = r15
            r7 = r16
            r8 = r17
            r9 = r18
            r1.requestSync(r2, r3, r4, r5, r6, r7, r8, r9)
        L_0x0076:
            r13.reportChange(r12, r15)
            android.content.Context r0 = r10.mContext
            android.content.Intent r1 = android.content.ContentResolver.ACTION_SYNC_CONN_STATUS_CHANGED
            r0.sendBroadcast(r1)
            r13.queueBackup()
            return
        L_0x0084:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0084 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.setMasterSyncAutomatically(boolean, int, int, int, int):void");
    }

    public boolean getMasterSyncAutomatically(int userId) {
        boolean booleanValue;
        synchronized (this.mAuthorities) {
            Boolean auto = this.mMasterSyncAutomatically.get(userId);
            booleanValue = auto == null ? this.mDefaultMasterSyncAutomatically : auto.booleanValue();
        }
        return booleanValue;
    }

    public int getAuthorityCount() {
        int size;
        synchronized (this.mAuthorities) {
            size = this.mAuthorities.size();
        }
        return size;
    }

    public AuthorityInfo getAuthority(int authorityId) {
        AuthorityInfo authorityInfo;
        synchronized (this.mAuthorities) {
            authorityInfo = this.mAuthorities.get(authorityId);
        }
        return authorityInfo;
    }

    public boolean isSyncActive(EndPoint info) {
        synchronized (this.mAuthorities) {
            for (SyncInfo syncInfo : getCurrentSyncs(info.userId)) {
                AuthorityInfo ainfo = getAuthority(syncInfo.authorityId);
                if (ainfo != null && ainfo.target.matchesSpec(info)) {
                    return true;
                }
            }
            return false;
        }
    }

    public void markPending(EndPoint info, boolean pendingValue) {
        synchronized (this.mAuthorities) {
            AuthorityInfo authority = getOrCreateAuthorityLocked(info, -1, true);
            if (authority != null) {
                getOrCreateSyncStatusLocked(authority.ident).pending = pendingValue;
                reportChange(2, info.userId);
            }
        }
    }

    public void removeStaleAccounts(Account[] currentAccounts, int userId) {
        synchronized (this.mAuthorities) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "Updating for new accounts...");
            }
            SparseArray<AuthorityInfo> removing = new SparseArray<>();
            Iterator<AccountInfo> accIt = this.mAccounts.values().iterator();
            while (accIt.hasNext()) {
                AccountInfo acc = accIt.next();
                if (acc.accountAndUser.userId == userId) {
                    if (currentAccounts == null || !ArrayUtils.contains(currentAccounts, acc.accountAndUser.account)) {
                        if (Log.isLoggable("SyncManager", 2)) {
                            Slog.v("SyncManager", "Account removed: " + acc.accountAndUser);
                        }
                        for (AuthorityInfo auth : acc.authorities.values()) {
                            removing.put(auth.ident, auth);
                        }
                        accIt.remove();
                    }
                }
            }
            int i = removing.size();
            if (i > 0) {
                while (i > 0) {
                    i--;
                    int ident = removing.keyAt(i);
                    AuthorityInfo auth2 = removing.valueAt(i);
                    if (this.mAuthorityRemovedListener != null) {
                        this.mAuthorityRemovedListener.onAuthorityRemoved(auth2.target);
                    }
                    this.mAuthorities.remove(ident);
                    int j = this.mSyncStatus.size();
                    while (j > 0) {
                        j--;
                        if (this.mSyncStatus.keyAt(j) == ident) {
                            this.mSyncStatus.remove(this.mSyncStatus.keyAt(j));
                        }
                    }
                    int j2 = this.mSyncHistory.size();
                    while (j2 > 0) {
                        j2--;
                        if (this.mSyncHistory.get(j2).authorityId == ident) {
                            this.mSyncHistory.remove(j2);
                        }
                    }
                }
                writeAccountInfoLocked();
                writeStatusLocked();
                writeStatisticsLocked();
            }
            SyncStorageEngineInjector.doDatabaseCleanupLocked(currentAccounts, userId);
        }
    }

    public SyncInfo addActiveSync(SyncManager.ActiveSyncContext activeSyncContext) {
        SyncInfo syncInfo;
        synchronized (this.mAuthorities) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "setActiveSync: account= auth=" + activeSyncContext.mSyncOperation.target + " src=" + activeSyncContext.mSyncOperation.syncSource + " extras=" + activeSyncContext.mSyncOperation.extras);
            }
            AuthorityInfo authorityInfo = getOrCreateAuthorityLocked(activeSyncContext.mSyncOperation.target, -1, true);
            syncInfo = new SyncInfo(authorityInfo.ident, authorityInfo.target.account, authorityInfo.target.provider, activeSyncContext.mStartTime);
            getCurrentSyncs(authorityInfo.target.userId).add(syncInfo);
        }
        reportActiveChange(activeSyncContext.mSyncOperation.target.userId);
        return syncInfo;
    }

    public void removeActiveSync(SyncInfo syncInfo, int userId) {
        synchronized (this.mAuthorities) {
            if (Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", "removeActiveSync: account=" + syncInfo.account + " user=" + userId + " auth=" + syncInfo.authority);
            }
            getCurrentSyncs(userId).remove(syncInfo);
        }
        reportActiveChange(userId);
    }

    public void reportActiveChange(int userId) {
        reportChange(4, userId);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a4, code lost:
        reportChange(8, r9.target.userId);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x00ad, code lost:
        return r4;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long insertStartSyncEvent(com.android.server.content.SyncOperation r9, long r10) {
        /*
            r8 = this;
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r0 = r8.mAuthorities
            monitor-enter(r0)
            java.lang.String r1 = "SyncManager"
            r2 = 2
            boolean r1 = android.util.Log.isLoggable(r1, r2)     // Catch:{ all -> 0x00ae }
            if (r1 == 0) goto L_0x0023
            java.lang.String r1 = "SyncManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ae }
            r3.<init>()     // Catch:{ all -> 0x00ae }
            java.lang.String r4 = "insertStartSyncEvent: "
            r3.append(r4)     // Catch:{ all -> 0x00ae }
            r3.append(r9)     // Catch:{ all -> 0x00ae }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00ae }
            android.util.Slog.v(r1, r3)     // Catch:{ all -> 0x00ae }
        L_0x0023:
            com.android.server.content.SyncStorageEngine$EndPoint r1 = r9.target     // Catch:{ all -> 0x00ae }
            java.lang.String r3 = "insertStartSyncEvent"
            com.android.server.content.SyncStorageEngine$AuthorityInfo r1 = r8.getAuthorityLocked(r1, r3)     // Catch:{ all -> 0x00ae }
            if (r1 != 0) goto L_0x0032
            r2 = -1
            monitor-exit(r0)     // Catch:{ all -> 0x00ae }
            return r2
        L_0x0032:
            com.android.server.content.SyncStorageEngine$SyncHistoryItem r3 = new com.android.server.content.SyncStorageEngine$SyncHistoryItem     // Catch:{ all -> 0x00ae }
            r3.<init>()     // Catch:{ all -> 0x00ae }
            boolean r4 = r9.isInitialization()     // Catch:{ all -> 0x00ae }
            r3.initialization = r4     // Catch:{ all -> 0x00ae }
            int r4 = r1.ident     // Catch:{ all -> 0x00ae }
            r3.authorityId = r4     // Catch:{ all -> 0x00ae }
            int r4 = r8.mNextHistoryId     // Catch:{ all -> 0x00ae }
            int r5 = r4 + 1
            r8.mNextHistoryId = r5     // Catch:{ all -> 0x00ae }
            r3.historyId = r4     // Catch:{ all -> 0x00ae }
            int r4 = r8.mNextHistoryId     // Catch:{ all -> 0x00ae }
            r5 = 0
            if (r4 >= 0) goto L_0x0050
            r8.mNextHistoryId = r5     // Catch:{ all -> 0x00ae }
        L_0x0050:
            r3.eventTime = r10     // Catch:{ all -> 0x00ae }
            int r4 = r9.syncSource     // Catch:{ all -> 0x00ae }
            r3.source = r4     // Catch:{ all -> 0x00ae }
            int r4 = r9.reason     // Catch:{ all -> 0x00ae }
            r3.reason = r4     // Catch:{ all -> 0x00ae }
            android.os.Bundle r4 = r9.extras     // Catch:{ all -> 0x00ae }
            r3.extras = r4     // Catch:{ all -> 0x00ae }
            r3.event = r5     // Catch:{ all -> 0x00ae }
            int r4 = r9.syncExemptionFlag     // Catch:{ all -> 0x00ae }
            r3.syncExemptionFlag = r4     // Catch:{ all -> 0x00ae }
            java.util.ArrayList<com.android.server.content.SyncStorageEngine$SyncHistoryItem> r4 = r8.mSyncHistory     // Catch:{ all -> 0x00ae }
            r4.add(r5, r3)     // Catch:{ all -> 0x00ae }
        L_0x0069:
            java.util.ArrayList<com.android.server.content.SyncStorageEngine$SyncHistoryItem> r4 = r8.mSyncHistory     // Catch:{ all -> 0x00ae }
            int r4 = r4.size()     // Catch:{ all -> 0x00ae }
            r5 = 100
            if (r4 <= r5) goto L_0x0081
            java.util.ArrayList<com.android.server.content.SyncStorageEngine$SyncHistoryItem> r4 = r8.mSyncHistory     // Catch:{ all -> 0x00ae }
            java.util.ArrayList<com.android.server.content.SyncStorageEngine$SyncHistoryItem> r5 = r8.mSyncHistory     // Catch:{ all -> 0x00ae }
            int r5 = r5.size()     // Catch:{ all -> 0x00ae }
            int r5 = r5 + -1
            r4.remove(r5)     // Catch:{ all -> 0x00ae }
            goto L_0x0069
        L_0x0081:
            int r4 = r3.historyId     // Catch:{ all -> 0x00ae }
            long r4 = (long) r4     // Catch:{ all -> 0x00ae }
            java.lang.String r6 = "SyncManager"
            boolean r2 = android.util.Log.isLoggable(r6, r2)     // Catch:{ all -> 0x00ae }
            if (r2 == 0) goto L_0x00a3
            java.lang.String r2 = "SyncManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ae }
            r6.<init>()     // Catch:{ all -> 0x00ae }
            java.lang.String r7 = "returning historyId "
            r6.append(r7)     // Catch:{ all -> 0x00ae }
            r6.append(r4)     // Catch:{ all -> 0x00ae }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ae }
            android.util.Slog.v(r2, r6)     // Catch:{ all -> 0x00ae }
        L_0x00a3:
            monitor-exit(r0)     // Catch:{ all -> 0x00ae }
            r0 = 8
            com.android.server.content.SyncStorageEngine$EndPoint r1 = r9.target
            int r1 = r1.userId
            r8.reportChange(r0, r1)
            return r4
        L_0x00ae:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00ae }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.insertStartSyncEvent(com.android.server.content.SyncOperation, long):long");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:84:0x02a3, code lost:
        reportChange(8, r32);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x02aa, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x0178 A[Catch:{ all -> 0x02ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x019a A[Catch:{ all -> 0x02ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x022a A[Catch:{ all -> 0x02ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0261 A[Catch:{ all -> 0x02ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0267 A[Catch:{ all -> 0x02ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0286 A[Catch:{ all -> 0x02ab }] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x028a A[Catch:{ all -> 0x02ab }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void stopSyncEvent(long r23, long r25, java.lang.String r27, long r28, long r30, int r32, android.content.SyncResult r33) {
        /*
            r22 = this;
            r1 = r22
            r2 = r23
            r4 = r25
            r6 = r27
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r7 = r1.mAuthorities
            monitor-enter(r7)
            java.lang.String r0 = "SyncManager"
            r8 = 2
            boolean r0 = android.util.Log.isLoggable(r0, r8)     // Catch:{ all -> 0x02ad }
            if (r0 == 0) goto L_0x002b
            java.lang.String r0 = "SyncManager"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ad }
            r9.<init>()     // Catch:{ all -> 0x02ad }
            java.lang.String r10 = "stopSyncEvent: historyId="
            r9.append(r10)     // Catch:{ all -> 0x02ad }
            r9.append(r2)     // Catch:{ all -> 0x02ad }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x02ad }
            android.util.Slog.v(r0, r9)     // Catch:{ all -> 0x02ad }
        L_0x002b:
            r0 = 0
            java.util.ArrayList<com.android.server.content.SyncStorageEngine$SyncHistoryItem> r9 = r1.mSyncHistory     // Catch:{ all -> 0x02ad }
            int r9 = r9.size()     // Catch:{ all -> 0x02ad }
        L_0x0032:
            if (r9 <= 0) goto L_0x0049
            int r9 = r9 + -1
            java.util.ArrayList<com.android.server.content.SyncStorageEngine$SyncHistoryItem> r10 = r1.mSyncHistory     // Catch:{ all -> 0x02ad }
            java.lang.Object r10 = r10.get(r9)     // Catch:{ all -> 0x02ad }
            com.android.server.content.SyncStorageEngine$SyncHistoryItem r10 = (com.android.server.content.SyncStorageEngine.SyncHistoryItem) r10     // Catch:{ all -> 0x02ad }
            r0 = r10
            int r10 = r0.historyId     // Catch:{ all -> 0x02ad }
            long r10 = (long) r10     // Catch:{ all -> 0x02ad }
            int r10 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r10 != 0) goto L_0x0047
            goto L_0x0049
        L_0x0047:
            r0 = 0
            goto L_0x0032
        L_0x0049:
            if (r0 != 0) goto L_0x0064
            java.lang.String r8 = "SyncManager"
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ad }
            r10.<init>()     // Catch:{ all -> 0x02ad }
            java.lang.String r11 = "stopSyncEvent: no history for id "
            r10.append(r11)     // Catch:{ all -> 0x02ad }
            r10.append(r2)     // Catch:{ all -> 0x02ad }
            java.lang.String r10 = r10.toString()     // Catch:{ all -> 0x02ad }
            android.util.Slog.w(r8, r10)     // Catch:{ all -> 0x02ad }
            monitor-exit(r7)     // Catch:{ all -> 0x02ad }
            return
        L_0x0064:
            r0.elapsedTime = r4     // Catch:{ all -> 0x02ad }
            r10 = 1
            r0.event = r10     // Catch:{ all -> 0x02ad }
            r0.mesg = r6     // Catch:{ all -> 0x02ad }
            r11 = r28
            r0.downstreamActivity = r11     // Catch:{ all -> 0x02ad }
            r13 = r30
            r0.upstreamActivity = r13     // Catch:{ all -> 0x02ab }
            int r15 = r0.authorityId     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo r15 = r1.getOrCreateSyncStatusLocked(r15)     // Catch:{ all -> 0x02ab }
            boolean r8 = r22.isClockValid()     // Catch:{ all -> 0x02ab }
            r10 = 0
            r15.maybeResetTodayStats(r8, r10)     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r8 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r10 = r8.numSyncs     // Catch:{ all -> 0x02ab }
            r16 = 1
            int r10 = r10 + 1
            r8.numSyncs = r10     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r8 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r10 = r8.numSyncs     // Catch:{ all -> 0x02ab }
            int r10 = r10 + 1
            r8.numSyncs = r10     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r8 = r15.totalStats     // Catch:{ all -> 0x02ab }
            long r2 = r8.totalElapsedTime     // Catch:{ all -> 0x02ab }
            long r2 = r2 + r4
            r8.totalElapsedTime = r2     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r2 = r15.todayStats     // Catch:{ all -> 0x02ab }
            r3 = r9
            long r8 = r2.totalElapsedTime     // Catch:{ all -> 0x02ab }
            long r8 = r8 + r4
            r2.totalElapsedTime = r8     // Catch:{ all -> 0x02ab }
            int r2 = r0.source     // Catch:{ all -> 0x02ab }
            if (r2 == 0) goto L_0x0106
            r8 = 1
            if (r2 == r8) goto L_0x00f6
            r8 = 2
            if (r2 == r8) goto L_0x00e6
            r8 = 3
            if (r2 == r8) goto L_0x00d6
            r8 = 4
            if (r2 == r8) goto L_0x00c6
            r8 = 5
            if (r2 == r8) goto L_0x00b6
            goto L_0x0116
        L_0x00b6:
            android.content.SyncStatusInfo$Stats r2 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourceFeed     // Catch:{ all -> 0x02ab }
            r9 = 1
            int r8 = r8 + r9
            r2.numSourceFeed = r8     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r2 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourceFeed     // Catch:{ all -> 0x02ab }
            int r8 = r8 + r9
            r2.numSourceFeed = r8     // Catch:{ all -> 0x02ab }
            goto L_0x0116
        L_0x00c6:
            android.content.SyncStatusInfo$Stats r2 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourcePeriodic     // Catch:{ all -> 0x02ab }
            r9 = 1
            int r8 = r8 + r9
            r2.numSourcePeriodic = r8     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r2 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourcePeriodic     // Catch:{ all -> 0x02ab }
            int r8 = r8 + r9
            r2.numSourcePeriodic = r8     // Catch:{ all -> 0x02ab }
            goto L_0x0116
        L_0x00d6:
            android.content.SyncStatusInfo$Stats r2 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourceUser     // Catch:{ all -> 0x02ab }
            r9 = 1
            int r8 = r8 + r9
            r2.numSourceUser = r8     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r2 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourceUser     // Catch:{ all -> 0x02ab }
            int r8 = r8 + r9
            r2.numSourceUser = r8     // Catch:{ all -> 0x02ab }
            goto L_0x0116
        L_0x00e6:
            android.content.SyncStatusInfo$Stats r2 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourcePoll     // Catch:{ all -> 0x02ab }
            r9 = 1
            int r8 = r8 + r9
            r2.numSourcePoll = r8     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r2 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourcePoll     // Catch:{ all -> 0x02ab }
            int r8 = r8 + r9
            r2.numSourcePoll = r8     // Catch:{ all -> 0x02ab }
            goto L_0x0116
        L_0x00f6:
            android.content.SyncStatusInfo$Stats r2 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourceLocal     // Catch:{ all -> 0x02ab }
            r9 = 1
            int r8 = r8 + r9
            r2.numSourceLocal = r8     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r2 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourceLocal     // Catch:{ all -> 0x02ab }
            int r8 = r8 + r9
            r2.numSourceLocal = r8     // Catch:{ all -> 0x02ab }
            goto L_0x0116
        L_0x0106:
            android.content.SyncStatusInfo$Stats r2 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourceOther     // Catch:{ all -> 0x02ab }
            r9 = 1
            int r8 = r8 + r9
            r2.numSourceOther = r8     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r2 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r8 = r2.numSourceOther     // Catch:{ all -> 0x02ab }
            int r8 = r8 + r9
            r2.numSourceOther = r8     // Catch:{ all -> 0x02ab }
        L_0x0116:
            r2 = 0
            int r8 = r22.getCurrentDayLocked()     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncStorageEngine$DayStats[] r9 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            r10 = 0
            r9 = r9[r10]     // Catch:{ all -> 0x02ab }
            if (r9 != 0) goto L_0x0133
            com.android.server.content.SyncStorageEngine$DayStats[] r9 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncStorageEngine$DayStats r10 = new com.android.server.content.SyncStorageEngine$DayStats     // Catch:{ all -> 0x02ab }
            r10.<init>(r8)     // Catch:{ all -> 0x02ab }
            r17 = 0
            r9[r17] = r10     // Catch:{ all -> 0x02ab }
            r18 = r2
            r19 = r3
            r3 = 0
            goto L_0x0163
        L_0x0133:
            com.android.server.content.SyncStorageEngine$DayStats[] r9 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            r10 = 0
            r9 = r9[r10]     // Catch:{ all -> 0x02ab }
            int r9 = r9.day     // Catch:{ all -> 0x02ab }
            if (r8 == r9) goto L_0x015a
            com.android.server.content.SyncStorageEngine$DayStats[] r9 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncStorageEngine$DayStats[] r10 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            r18 = r2
            com.android.server.content.SyncStorageEngine$DayStats[] r2 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            int r2 = r2.length     // Catch:{ all -> 0x02ab }
            r19 = r3
            r3 = 1
            int r2 = r2 - r3
            r11 = 0
            java.lang.System.arraycopy(r9, r11, r10, r3, r2)     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncStorageEngine$DayStats[] r2 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncStorageEngine$DayStats r3 = new com.android.server.content.SyncStorageEngine$DayStats     // Catch:{ all -> 0x02ab }
            r3.<init>(r8)     // Catch:{ all -> 0x02ab }
            r9 = 0
            r2[r9] = r3     // Catch:{ all -> 0x02ab }
            r2 = 1
            r3 = 0
            goto L_0x0165
        L_0x015a:
            r18 = r2
            r19 = r3
            com.android.server.content.SyncStorageEngine$DayStats[] r2 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            r3 = 0
            r2 = r2[r3]     // Catch:{ all -> 0x02ab }
        L_0x0163:
            r2 = r18
        L_0x0165:
            com.android.server.content.SyncStorageEngine$DayStats[] r9 = r1.mDayStats     // Catch:{ all -> 0x02ab }
            r3 = r9[r3]     // Catch:{ all -> 0x02ab }
            long r9 = r0.eventTime     // Catch:{ all -> 0x02ab }
            long r9 = r9 + r4
            r11 = 0
            java.lang.String r12 = "success"
            boolean r12 = r12.equals(r6)     // Catch:{ all -> 0x02ab }
            r17 = 0
            if (r12 == 0) goto L_0x019a
            r20 = r11
            long r11 = r15.lastSuccessTime     // Catch:{ all -> 0x02ab }
            int r11 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
            if (r11 == 0) goto L_0x0186
            long r11 = r15.lastFailureTime     // Catch:{ all -> 0x02ab }
            int r11 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
            if (r11 == 0) goto L_0x0189
        L_0x0186:
            r11 = 1
            r20 = r11
        L_0x0189:
            int r11 = r0.source     // Catch:{ all -> 0x02ab }
            r15.setLastSuccess(r11, r9)     // Catch:{ all -> 0x02ab }
            int r11 = r3.successCount     // Catch:{ all -> 0x02ab }
            r12 = 1
            int r11 = r11 + r12
            r3.successCount = r11     // Catch:{ all -> 0x02ab }
            long r11 = r3.successTime     // Catch:{ all -> 0x02ab }
            long r11 = r11 + r4
            r3.successTime = r11     // Catch:{ all -> 0x02ab }
            goto L_0x01e4
        L_0x019a:
            r20 = r11
            java.lang.String r11 = "canceled"
            boolean r11 = r11.equals(r6)     // Catch:{ all -> 0x02ab }
            if (r11 != 0) goto L_0x01d0
            long r11 = r15.lastFailureTime     // Catch:{ all -> 0x02ab }
            int r11 = (r11 > r17 ? 1 : (r11 == r17 ? 0 : -1))
            if (r11 != 0) goto L_0x01ad
            r11 = 1
            r20 = r11
        L_0x01ad:
            android.content.SyncStatusInfo$Stats r11 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r12 = r11.numFailures     // Catch:{ all -> 0x02ab }
            r16 = 1
            int r12 = r12 + 1
            r11.numFailures = r12     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r11 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r12 = r11.numFailures     // Catch:{ all -> 0x02ab }
            int r12 = r12 + 1
            r11.numFailures = r12     // Catch:{ all -> 0x02ab }
            int r11 = r0.source     // Catch:{ all -> 0x02ab }
            r15.setLastFailure(r11, r9, r6)     // Catch:{ all -> 0x02ab }
            int r11 = r3.failureCount     // Catch:{ all -> 0x02ab }
            r12 = 1
            int r11 = r11 + r12
            r3.failureCount = r11     // Catch:{ all -> 0x02ab }
            long r11 = r3.failureTime     // Catch:{ all -> 0x02ab }
            long r11 = r11 + r4
            r3.failureTime = r11     // Catch:{ all -> 0x02ab }
            goto L_0x01e4
        L_0x01d0:
            android.content.SyncStatusInfo$Stats r11 = r15.totalStats     // Catch:{ all -> 0x02ab }
            int r12 = r11.numCancels     // Catch:{ all -> 0x02ab }
            r16 = 1
            int r12 = r12 + 1
            r11.numCancels = r12     // Catch:{ all -> 0x02ab }
            android.content.SyncStatusInfo$Stats r11 = r15.todayStats     // Catch:{ all -> 0x02ab }
            int r12 = r11.numCancels     // Catch:{ all -> 0x02ab }
            int r12 = r12 + 1
            r11.numCancels = r12     // Catch:{ all -> 0x02ab }
            r20 = 1
        L_0x01e4:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ab }
            r11.<init>()     // Catch:{ all -> 0x02ab }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ab }
            r12.<init>()     // Catch:{ all -> 0x02ab }
            r17 = r3
            java.lang.String r3 = ""
            r12.append(r3)     // Catch:{ all -> 0x02ab }
            r12.append(r6)     // Catch:{ all -> 0x02ab }
            java.lang.String r3 = " Source="
            r12.append(r3)     // Catch:{ all -> 0x02ab }
            java.lang.String[] r3 = SOURCES     // Catch:{ all -> 0x02ab }
            r18 = r8
            int r8 = r0.source     // Catch:{ all -> 0x02ab }
            r3 = r3[r8]     // Catch:{ all -> 0x02ab }
            r12.append(r3)     // Catch:{ all -> 0x02ab }
            java.lang.String r3 = " Elapsed="
            r12.append(r3)     // Catch:{ all -> 0x02ab }
            java.lang.String r3 = r12.toString()     // Catch:{ all -> 0x02ab }
            r11.append(r3)     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncManager.formatDurationHMS(r11, r4)     // Catch:{ all -> 0x02ab }
            java.lang.String r3 = " Reason="
            r11.append(r3)     // Catch:{ all -> 0x02ab }
            r3 = 0
            int r8 = r0.reason     // Catch:{ all -> 0x02ab }
            java.lang.String r3 = com.android.server.content.SyncOperation.reasonToString(r3, r8)     // Catch:{ all -> 0x02ab }
            r11.append(r3)     // Catch:{ all -> 0x02ab }
            int r3 = r0.syncExemptionFlag     // Catch:{ all -> 0x02ab }
            if (r3 == 0) goto L_0x0249
            java.lang.String r3 = " Exemption="
            r11.append(r3)     // Catch:{ all -> 0x02ab }
            int r3 = r0.syncExemptionFlag     // Catch:{ all -> 0x02ab }
            r8 = 1
            if (r3 == r8) goto L_0x0244
            r8 = 2
            if (r3 == r8) goto L_0x023d
            int r3 = r0.syncExemptionFlag     // Catch:{ all -> 0x02ab }
            r11.append(r3)     // Catch:{ all -> 0x02ab }
            goto L_0x0249
        L_0x023d:
            java.lang.String r3 = "top"
            r11.append(r3)     // Catch:{ all -> 0x02ab }
            goto L_0x0249
        L_0x0244:
            java.lang.String r3 = "fg"
            r11.append(r3)     // Catch:{ all -> 0x02ab }
        L_0x0249:
            java.lang.String r3 = " Extras="
            r11.append(r3)     // Catch:{ all -> 0x02ab }
            android.os.Bundle r3 = r0.extras     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncOperation.extrasToStringBuilder(r3, r11)     // Catch:{ all -> 0x02ab }
            java.lang.String r3 = r11.toString()     // Catch:{ all -> 0x02ab }
            r15.addEvent(r3)     // Catch:{ all -> 0x02ab }
            r3 = r33
            com.android.server.content.SyncStorageEngineInjector.updateResultStatusLocked(r15, r6, r3)     // Catch:{ all -> 0x02ab }
            if (r20 == 0) goto L_0x0267
            r22.writeStatusLocked()     // Catch:{ all -> 0x02ab }
            r21 = r0
            goto L_0x0284
        L_0x0267:
            com.android.server.content.SyncStorageEngine$MyHandler r8 = r1.mHandler     // Catch:{ all -> 0x02ab }
            r12 = 1
            boolean r8 = r8.hasMessages(r12)     // Catch:{ all -> 0x02ab }
            if (r8 != 0) goto L_0x0282
            com.android.server.content.SyncStorageEngine$MyHandler r8 = r1.mHandler     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncStorageEngine$MyHandler r12 = r1.mHandler     // Catch:{ all -> 0x02ab }
            r21 = r0
            r0 = 1
            android.os.Message r0 = r12.obtainMessage(r0)     // Catch:{ all -> 0x02ab }
            r3 = 600000(0x927c0, double:2.964394E-318)
            r8.sendMessageDelayed(r0, r3)     // Catch:{ all -> 0x02ab }
            goto L_0x0284
        L_0x0282:
            r21 = r0
        L_0x0284:
            if (r2 == 0) goto L_0x028a
            r22.writeStatisticsLocked()     // Catch:{ all -> 0x02ab }
            goto L_0x02a2
        L_0x028a:
            com.android.server.content.SyncStorageEngine$MyHandler r0 = r1.mHandler     // Catch:{ all -> 0x02ab }
            r3 = 2
            boolean r0 = r0.hasMessages(r3)     // Catch:{ all -> 0x02ab }
            if (r0 != 0) goto L_0x02a2
            com.android.server.content.SyncStorageEngine$MyHandler r0 = r1.mHandler     // Catch:{ all -> 0x02ab }
            com.android.server.content.SyncStorageEngine$MyHandler r3 = r1.mHandler     // Catch:{ all -> 0x02ab }
            r4 = 2
            android.os.Message r3 = r3.obtainMessage(r4)     // Catch:{ all -> 0x02ab }
            r4 = 1800000(0x1b7740, double:8.89318E-318)
            r0.sendMessageDelayed(r3, r4)     // Catch:{ all -> 0x02ab }
        L_0x02a2:
            monitor-exit(r7)     // Catch:{ all -> 0x02ab }
            r0 = 8
            r2 = r32
            r1.reportChange(r0, r2)
            return
        L_0x02ab:
            r0 = move-exception
            goto L_0x02b0
        L_0x02ad:
            r0 = move-exception
            r13 = r30
        L_0x02b0:
            r2 = r32
        L_0x02b2:
            monitor-exit(r7)     // Catch:{ all -> 0x02b4 }
            throw r0
        L_0x02b4:
            r0 = move-exception
            goto L_0x02b2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.stopSyncEvent(long, long, java.lang.String, long, long, int, android.content.SyncResult):void");
    }

    private List<SyncInfo> getCurrentSyncs(int userId) {
        List<SyncInfo> currentSyncsLocked;
        synchronized (this.mAuthorities) {
            currentSyncsLocked = getCurrentSyncsLocked(userId);
        }
        return currentSyncsLocked;
    }

    public List<SyncInfo> getCurrentSyncsCopy(int userId, boolean canAccessAccounts) {
        List<SyncInfo> syncsCopy;
        SyncInfo copy;
        synchronized (this.mAuthorities) {
            List<SyncInfo> syncs = getCurrentSyncsLocked(userId);
            syncsCopy = new ArrayList<>();
            for (SyncInfo sync : syncs) {
                if (!canAccessAccounts) {
                    copy = SyncInfo.createAccountRedacted(sync.authorityId, sync.authority, sync.startTime);
                } else {
                    copy = new SyncInfo(sync);
                }
                syncsCopy.add(copy);
            }
        }
        return syncsCopy;
    }

    private List<SyncInfo> getCurrentSyncsLocked(int userId) {
        ArrayList<SyncInfo> syncs = this.mCurrentSyncs.get(userId);
        if (syncs != null) {
            return syncs;
        }
        ArrayList<SyncInfo> syncs2 = new ArrayList<>();
        this.mCurrentSyncs.put(userId, syncs2);
        return syncs2;
    }

    public Pair<AuthorityInfo, SyncStatusInfo> getCopyOfAuthorityWithSyncStatus(EndPoint info) {
        Pair<AuthorityInfo, SyncStatusInfo> createCopyPairOfAuthorityWithSyncStatusLocked;
        synchronized (this.mAuthorities) {
            createCopyPairOfAuthorityWithSyncStatusLocked = createCopyPairOfAuthorityWithSyncStatusLocked(getOrCreateAuthorityLocked(info, -1, true));
        }
        return createCopyPairOfAuthorityWithSyncStatusLocked;
    }

    public SyncStatusInfo getStatusByAuthority(EndPoint info) {
        if (info.account == null || info.provider == null) {
            return null;
        }
        synchronized (this.mAuthorities) {
            int N = this.mSyncStatus.size();
            for (int i = 0; i < N; i++) {
                SyncStatusInfo cur = this.mSyncStatus.valueAt(i);
                AuthorityInfo ainfo = this.mAuthorities.get(cur.authorityId);
                if (ainfo != null && ainfo.target.matchesSpec(info)) {
                    return cur;
                }
            }
            return null;
        }
    }

    public boolean isSyncPending(EndPoint info) {
        synchronized (this.mAuthorities) {
            int N = this.mSyncStatus.size();
            for (int i = 0; i < N; i++) {
                SyncStatusInfo cur = this.mSyncStatus.valueAt(i);
                AuthorityInfo ainfo = this.mAuthorities.get(cur.authorityId);
                if (ainfo != null) {
                    if (ainfo.target.matchesSpec(info)) {
                        if (cur.pending) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public ArrayList<SyncHistoryItem> getSyncHistory() {
        ArrayList<SyncHistoryItem> items;
        synchronized (this.mAuthorities) {
            int N = this.mSyncHistory.size();
            items = new ArrayList<>(N);
            for (int i = 0; i < N; i++) {
                items.add(this.mSyncHistory.get(i));
            }
        }
        return items;
    }

    public DayStats[] getDayStatistics() {
        DayStats[] ds;
        synchronized (this.mAuthorities) {
            ds = new DayStats[this.mDayStats.length];
            System.arraycopy(this.mDayStats, 0, ds, 0, ds.length);
        }
        return ds;
    }

    private Pair<AuthorityInfo, SyncStatusInfo> createCopyPairOfAuthorityWithSyncStatusLocked(AuthorityInfo authorityInfo) {
        return Pair.create(new AuthorityInfo(authorityInfo), new SyncStatusInfo(getOrCreateSyncStatusLocked(authorityInfo.ident)));
    }

    private int getCurrentDayLocked() {
        this.mCal.setTimeInMillis(System.currentTimeMillis());
        int dayOfYear = this.mCal.get(6);
        if (this.mYear != this.mCal.get(1)) {
            this.mYear = this.mCal.get(1);
            this.mCal.clear();
            this.mCal.set(1, this.mYear);
            this.mYearInDays = (int) (this.mCal.getTimeInMillis() / 86400000);
        }
        return this.mYearInDays + dayOfYear;
    }

    private AuthorityInfo getAuthorityLocked(EndPoint info, String tag) {
        AccountAndUser au = new AccountAndUser(info.account, info.userId);
        AccountInfo accountInfo = this.mAccounts.get(au);
        if (accountInfo == null) {
            if (tag != null && Log.isLoggable("SyncManager", 2)) {
                Slog.v("SyncManager", tag + ": unknown account " + au);
            }
            return null;
        }
        AuthorityInfo authority = accountInfo.authorities.get(info.provider);
        if (authority != null) {
            return authority;
        }
        if (tag != null && Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", tag + ": unknown provider " + info.provider);
        }
        return null;
    }

    private AuthorityInfo getOrCreateAuthorityLocked(EndPoint info, int ident, boolean doWrite) {
        AccountAndUser au = new AccountAndUser(info.account, info.userId);
        AccountInfo account = this.mAccounts.get(au);
        if (account == null) {
            account = new AccountInfo(au);
            this.mAccounts.put(au, account);
        }
        AuthorityInfo authority = account.authorities.get(info.provider);
        if (authority != null) {
            return authority;
        }
        AuthorityInfo authority2 = createAuthorityLocked(info, ident, doWrite);
        account.authorities.put(info.provider, authority2);
        return authority2;
    }

    private AuthorityInfo createAuthorityLocked(EndPoint info, int ident, boolean doWrite) {
        if (ident < 0) {
            ident = this.mNextAuthorityId;
            this.mNextAuthorityId++;
            doWrite = true;
        }
        if (Log.isLoggable("SyncManager", 2)) {
            Slog.v("SyncManager", "created a new AuthorityInfo for " + info);
        }
        AuthorityInfo authority = new AuthorityInfo(info, ident);
        this.mAuthorities.put(ident, authority);
        if (doWrite) {
            writeAccountInfoLocked();
        }
        return authority;
    }

    public void removeAuthority(EndPoint info) {
        synchronized (this.mAuthorities) {
            removeAuthorityLocked(info.account, info.userId, info.provider, true);
        }
    }

    private void removeAuthorityLocked(Account account, int userId, String authorityName, boolean doWrite) {
        AuthorityInfo authorityInfo;
        AccountInfo accountInfo = this.mAccounts.get(new AccountAndUser(account, userId));
        if (accountInfo != null && (authorityInfo = accountInfo.authorities.remove(authorityName)) != null) {
            OnAuthorityRemovedListener onAuthorityRemovedListener = this.mAuthorityRemovedListener;
            if (onAuthorityRemovedListener != null) {
                onAuthorityRemovedListener.onAuthorityRemoved(authorityInfo.target);
            }
            this.mAuthorities.remove(authorityInfo.ident);
            if (doWrite) {
                writeAccountInfoLocked();
            }
        }
    }

    private SyncStatusInfo getOrCreateSyncStatusLocked(int authorityId) {
        SyncStatusInfo status = this.mSyncStatus.get(authorityId);
        if (status != null) {
            return status;
        }
        SyncStatusInfo status2 = new SyncStatusInfo(authorityId);
        this.mSyncStatus.put(authorityId, status2);
        return status2;
    }

    public void writeAllState() {
        synchronized (this.mAuthorities) {
            writeStatusLocked();
            writeStatisticsLocked();
        }
    }

    public boolean shouldGrantSyncAdaptersAccountAccess() {
        return this.mGrantSyncAdaptersAccountAccess;
    }

    public void clearAndReadState() {
        synchronized (this.mAuthorities) {
            this.mAuthorities.clear();
            this.mAccounts.clear();
            this.mServices.clear();
            this.mSyncStatus.clear();
            this.mSyncHistory.clear();
            readAccountInfoLocked();
            readStatusLocked();
            readStatisticsLocked();
            writeAccountInfoLocked();
            writeStatusLocked();
            writeStatisticsLocked();
            SyncStorageEngineInjector.clearAndReadAndWriteLocked();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:137:0x01ba A[EDGE_INSN: B:137:0x01ba->B:100:0x01ba ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0112 A[Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3, all -> 0x01d0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x01a6 A[Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3, all -> 0x01d0 }] */
    /* JADX WARNING: Removed duplicated region for block: B:98:0x01b5 A[LOOP:1: B:66:0x010f->B:98:0x01b5, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readAccountInfoLocked() {
        /*
            r22 = this;
            r1 = r22
            java.lang.String r2 = "No initial accounts"
            java.lang.String r3 = "Error reading accounts"
            java.lang.String r0 = "SyncManagerFile"
            java.lang.String r4 = "SyncManager"
            r5 = -1
            r6 = 0
            android.util.AtomicFile r7 = r1.mAccountInfoFile     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            java.io.FileInputStream r7 = r7.openRead()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r6 = r7
            r7 = 2
            boolean r8 = android.util.Log.isLoggable(r0, r7)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r8 == 0) goto L_0x0034
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r8.<init>()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            java.lang.String r9 = "Reading "
            r8.append(r9)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            android.util.AtomicFile r9 = r1.mAccountInfoFile     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            java.io.File r9 = r9.getBaseFile()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r8.append(r9)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            java.lang.String r8 = r8.toString()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            android.util.Slog.v(r0, r8)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
        L_0x0034:
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r8 = r0
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            java.lang.String r0 = r0.name()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r8.setInput(r6, r0)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            int r0 = r8.getEventType()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r9 = r0
        L_0x0047:
            r10 = 1
            if (r9 == r7) goto L_0x0052
            if (r9 == r10) goto L_0x0052
            int r0 = r8.next()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r9 = r0
            goto L_0x0047
        L_0x0052:
            if (r9 != r10) goto L_0x0069
            android.util.Slog.i(r4, r2)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            int r0 = r5 + 1
            int r2 = r1.mNextAuthorityId
            int r0 = java.lang.Math.max(r0, r2)
            r1.mNextAuthorityId = r0
            if (r6 == 0) goto L_0x0068
            r6.close()     // Catch:{ IOException -> 0x0067 }
            goto L_0x0068
        L_0x0067:
            r0 = move-exception
        L_0x0068:
            return
        L_0x0069:
            java.lang.String r0 = r8.getName()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r11 = r0
            java.lang.String r0 = "accounts"
            boolean r0 = r0.equals(r11)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r0 == 0) goto L_0x01b9
            java.lang.String r0 = "listen-for-tickles"
            r12 = 0
            java.lang.String r0 = r8.getAttributeValue(r12, r0)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r13 = r0
            java.lang.String r0 = "version"
            java.lang.String r0 = r8.getAttributeValue(r12, r0)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r14 = r0
            if (r14 != 0) goto L_0x008b
            r0 = 0
            goto L_0x008f
        L_0x008b:
            int r0 = java.lang.Integer.parseInt(r14)     // Catch:{ NumberFormatException -> 0x0091 }
        L_0x008f:
            r7 = r0
            goto L_0x0098
        L_0x0091:
            r0 = move-exception
            r16 = r0
            r0 = r16
            r0 = 0
            r7 = r0
        L_0x0098:
            r15 = 3
            if (r7 >= r15) goto L_0x009d
            r1.mGrantSyncAdaptersAccountAccess = r10     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
        L_0x009d:
            java.lang.String r0 = "nextAuthorityId"
            java.lang.String r0 = r8.getAttributeValue(r12, r0)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r18 = r0
            if (r18 != 0) goto L_0x00aa
            r0 = 0
            goto L_0x00ae
        L_0x00aa:
            int r0 = java.lang.Integer.parseInt(r18)     // Catch:{ NumberFormatException -> 0x00b7 }
        L_0x00ae:
            int r10 = r1.mNextAuthorityId     // Catch:{ NumberFormatException -> 0x00b7 }
            int r10 = java.lang.Math.max(r10, r0)     // Catch:{ NumberFormatException -> 0x00b7 }
            r1.mNextAuthorityId = r10     // Catch:{ NumberFormatException -> 0x00b7 }
            goto L_0x00b8
        L_0x00b7:
            r0 = move-exception
        L_0x00b8:
            java.lang.String r0 = "offsetInSeconds"
            java.lang.String r0 = r8.getAttributeValue(r12, r0)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r10 = r0
            if (r10 != 0) goto L_0x00c4
            r0 = 0
            goto L_0x00c8
        L_0x00c4:
            int r0 = java.lang.Integer.parseInt(r10)     // Catch:{ NumberFormatException -> 0x00cb }
        L_0x00c8:
            r1.mSyncRandomOffset = r0     // Catch:{ NumberFormatException -> 0x00cb }
            goto L_0x00cf
        L_0x00cb:
            r0 = move-exception
            r12 = 0
            r1.mSyncRandomOffset = r12     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
        L_0x00cf:
            int r0 = r1.mSyncRandomOffset     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r0 != 0) goto L_0x00e9
            java.util.Random r0 = new java.util.Random     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r12 = r9
            r20 = r10
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r0.<init>(r9)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r9 = 86400(0x15180, float:1.21072E-40)
            int r9 = r0.nextInt(r9)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r1.mSyncRandomOffset = r9     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            goto L_0x00ec
        L_0x00e9:
            r12 = r9
            r20 = r10
        L_0x00ec:
            android.util.SparseArray<java.lang.Boolean> r0 = r1.mMasterSyncAutomatically     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r13 == 0) goto L_0x00f9
            boolean r9 = java.lang.Boolean.parseBoolean(r13)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r9 == 0) goto L_0x00f7
            goto L_0x00f9
        L_0x00f7:
            r9 = 0
            goto L_0x00fa
        L_0x00f9:
            r9 = 1
        L_0x00fa:
            java.lang.Boolean r9 = java.lang.Boolean.valueOf(r9)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r10 = 0
            r0.put(r10, r9)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            int r0 = r8.next()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r9 = 0
            r10 = 0
            com.android.server.content.SyncStorageEngine$AccountAuthorityValidator r12 = new com.android.server.content.SyncStorageEngine$AccountAuthorityValidator     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            android.content.Context r15 = r1.mContext     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r12.<init>(r15)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
        L_0x010f:
            r15 = 2
            if (r0 != r15) goto L_0x01a6
            java.lang.String r16 = r8.getName()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r11 = r16
            int r15 = r8.getDepth()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r21 = r13
            r13 = 2
            if (r15 != r13) goto L_0x0173
            java.lang.String r13 = "authority"
            boolean r13 = r13.equals(r11)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r13 == 0) goto L_0x0162
            com.android.server.content.SyncStorageEngine$AuthorityInfo r13 = r1.parseAuthority(r8, r7, r12)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r9 = r13
            r10 = 0
            if (r9 == 0) goto L_0x0143
            int r13 = r9.ident     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r13 <= r5) goto L_0x013e
            int r13 = r9.ident     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r5 = r13
            r16 = 2
            r17 = 0
            goto L_0x01ac
        L_0x013e:
            r16 = 2
            r17 = 0
            goto L_0x01ac
        L_0x0143:
            r15 = 3
            java.lang.Object[] r13 = new java.lang.Object[r15]     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            java.lang.String r15 = "26513719"
            r17 = 0
            r13[r17] = r15     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r15 = -1
            java.lang.Integer r15 = java.lang.Integer.valueOf(r15)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r19 = 1
            r13[r19] = r15     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            java.lang.String r15 = "Malformed authority"
            r16 = 2
            r13[r16] = r15     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r15 = 1397638484(0x534e4554, float:8.859264E11)
            android.util.EventLog.writeEvent(r15, r13)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            goto L_0x01ac
        L_0x0162:
            r16 = 2
            r17 = 0
            java.lang.String r13 = "listenForTickles"
            boolean r13 = r13.equals(r11)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r13 == 0) goto L_0x01ac
            r1.parseListenForTickles(r8)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            goto L_0x01ac
        L_0x0173:
            r16 = r13
            r17 = 0
            int r13 = r8.getDepth()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r15 = 3
            if (r13 != r15) goto L_0x018f
            java.lang.String r13 = "periodicSync"
            boolean r13 = r13.equals(r11)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r13 == 0) goto L_0x01ac
            if (r9 == 0) goto L_0x01ac
            android.content.PeriodicSync r13 = r1.parsePeriodicSync(r8, r9)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r10 = r13
            goto L_0x01ac
        L_0x018f:
            int r13 = r8.getDepth()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r15 = 4
            if (r13 != r15) goto L_0x01ac
            if (r10 == 0) goto L_0x01ac
            java.lang.String r13 = "extra"
            boolean r13 = r13.equals(r11)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            if (r13 == 0) goto L_0x01ac
            android.os.Bundle r13 = r10.extras     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r1.parseExtra(r8, r13)     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            goto L_0x01ac
        L_0x01a6:
            r21 = r13
            r16 = r15
            r17 = 0
        L_0x01ac:
            int r13 = r8.next()     // Catch:{ XmlPullParserException -> 0x01f0, IOException -> 0x01d3 }
            r0 = r13
            r13 = 1
            if (r0 != r13) goto L_0x01b5
            goto L_0x01ba
        L_0x01b5:
            r13 = r21
            goto L_0x010f
        L_0x01b9:
            r12 = r9
        L_0x01ba:
            int r0 = r5 + 1
            int r2 = r1.mNextAuthorityId
            int r0 = java.lang.Math.max(r0, r2)
            r1.mNextAuthorityId = r0
            if (r6 == 0) goto L_0x01cc
            r6.close()     // Catch:{ IOException -> 0x01ca }
        L_0x01c9:
            goto L_0x01cc
        L_0x01ca:
            r0 = move-exception
            goto L_0x01c9
        L_0x01cc:
            r22.maybeMigrateSettingsForRenamedAuthorities()
            return
        L_0x01d0:
            r0 = move-exception
            r2 = r0
            goto L_0x0207
        L_0x01d3:
            r0 = move-exception
            r7 = r0
            if (r6 != 0) goto L_0x01db
            android.util.Slog.i(r4, r2)     // Catch:{ all -> 0x01d0 }
            goto L_0x01de
        L_0x01db:
            android.util.Slog.w(r4, r3, r7)     // Catch:{ all -> 0x01d0 }
        L_0x01de:
            int r0 = r5 + 1
            int r2 = r1.mNextAuthorityId
            int r0 = java.lang.Math.max(r0, r2)
            r1.mNextAuthorityId = r0
            if (r6 == 0) goto L_0x01ef
            r6.close()     // Catch:{ IOException -> 0x01ee }
            goto L_0x01ef
        L_0x01ee:
            r0 = move-exception
        L_0x01ef:
            return
        L_0x01f0:
            r0 = move-exception
            r2 = r0
            android.util.Slog.w(r4, r3, r2)     // Catch:{ all -> 0x01d0 }
            int r0 = r5 + 1
            int r3 = r1.mNextAuthorityId
            int r0 = java.lang.Math.max(r0, r3)
            r1.mNextAuthorityId = r0
            if (r6 == 0) goto L_0x0206
            r6.close()     // Catch:{ IOException -> 0x0205 }
            goto L_0x0206
        L_0x0205:
            r0 = move-exception
        L_0x0206:
            return
        L_0x0207:
            int r0 = r5 + 1
            int r3 = r1.mNextAuthorityId
            int r0 = java.lang.Math.max(r0, r3)
            r1.mNextAuthorityId = r0
            if (r6 == 0) goto L_0x0218
            r6.close()     // Catch:{ IOException -> 0x0217 }
            goto L_0x0218
        L_0x0217:
            r0 = move-exception
        L_0x0218:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.readAccountInfoLocked():void");
    }

    private void maybeDeleteLegacyPendingInfoLocked(File syncDir) {
        File file = new File(syncDir, "pending.bin");
        if (file.exists()) {
            file.delete();
        }
    }

    private boolean maybeMigrateSettingsForRenamedAuthorities() {
        boolean writeNeeded = false;
        ArrayList<AuthorityInfo> authoritiesToRemove = new ArrayList<>();
        int N = this.mAuthorities.size();
        for (int i = 0; i < N; i++) {
            AuthorityInfo authority = this.mAuthorities.valueAt(i);
            String newAuthorityName = sAuthorityRenames.get(authority.target.provider);
            if (newAuthorityName != null) {
                authoritiesToRemove.add(authority);
                if (authority.enabled) {
                    EndPoint newInfo = new EndPoint(authority.target.account, newAuthorityName, authority.target.userId);
                    if (getAuthorityLocked(newInfo, "cleanup") == null) {
                        getOrCreateAuthorityLocked(newInfo, -1, false).enabled = true;
                        writeNeeded = true;
                    }
                }
            }
        }
        Iterator<AuthorityInfo> it = authoritiesToRemove.iterator();
        while (it.hasNext()) {
            AuthorityInfo authorityInfo = it.next();
            removeAuthorityLocked(authorityInfo.target.account, authorityInfo.target.userId, authorityInfo.target.provider, false);
            writeNeeded = true;
        }
        return writeNeeded;
    }

    private void parseListenForTickles(XmlPullParser parser) {
        int userId = 0;
        try {
            userId = Integer.parseInt(parser.getAttributeValue((String) null, XML_ATTR_USER));
        } catch (NumberFormatException e) {
            Slog.e("SyncManager", "error parsing the user for listen-for-tickles", e);
        } catch (NullPointerException e2) {
            Slog.e("SyncManager", "the user in listen-for-tickles is null", e2);
        }
        String enabled = parser.getAttributeValue((String) null, XML_ATTR_ENABLED);
        this.mMasterSyncAutomatically.put(userId, Boolean.valueOf(enabled == null || Boolean.parseBoolean(enabled)));
    }

    /* JADX WARNING: Removed duplicated region for block: B:43:0x0169  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x01a0  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.server.content.SyncStorageEngine.AuthorityInfo parseAuthority(org.xmlpull.v1.XmlPullParser r24, int r25, com.android.server.content.SyncStorageEngine.AccountAuthorityValidator r26) {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            r3 = r26
            java.lang.String r4 = "SyncManager"
            r5 = 0
            r6 = -1
            r7 = 0
            java.lang.String r0 = "id"
            java.lang.String r0 = r2.getAttributeValue(r7, r0)     // Catch:{ NumberFormatException -> 0x0020, NullPointerException -> 0x0018 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0020, NullPointerException -> 0x0018 }
            r6 = r0
        L_0x0017:
            goto L_0x0027
        L_0x0018:
            r0 = move-exception
            java.lang.String r8 = "the id of the authority is null"
            android.util.Slog.e(r4, r8, r0)
            goto L_0x0027
        L_0x0020:
            r0 = move-exception
            java.lang.String r8 = "error parsing the id of the authority"
            android.util.Slog.e(r4, r8, r0)
            goto L_0x0017
        L_0x0027:
            if (r6 < 0) goto L_0x01c3
            java.lang.String r0 = "authority"
            java.lang.String r8 = r2.getAttributeValue(r7, r0)
            java.lang.String r0 = "enabled"
            java.lang.String r9 = r2.getAttributeValue(r7, r0)
            java.lang.String r0 = "syncable"
            java.lang.String r0 = r2.getAttributeValue(r7, r0)
            java.lang.String r10 = "account"
            java.lang.String r10 = r2.getAttributeValue(r7, r10)
            java.lang.String r11 = "type"
            java.lang.String r11 = r2.getAttributeValue(r7, r11)
            java.lang.String r12 = "user"
            java.lang.String r12 = r2.getAttributeValue(r7, r12)
            java.lang.String r13 = "package"
            java.lang.String r13 = r2.getAttributeValue(r7, r13)
            java.lang.String r14 = "class"
            java.lang.String r7 = r2.getAttributeValue(r7, r14)
            if (r12 != 0) goto L_0x0061
            r15 = 0
            goto L_0x0065
        L_0x0061:
            int r15 = java.lang.Integer.parseInt(r12)
        L_0x0065:
            r14 = -1
            if (r11 != 0) goto L_0x0072
            if (r13 != 0) goto L_0x0072
            java.lang.String r11 = "com.google"
            java.lang.String r0 = java.lang.String.valueOf(r14)
            r14 = r0
            goto L_0x0073
        L_0x0072:
            r14 = r0
        L_0x0073:
            android.util.SparseArray<com.android.server.content.SyncStorageEngine$AuthorityInfo> r0 = r1.mAuthorities
            java.lang.Object r0 = r0.get(r6)
            com.android.server.content.SyncStorageEngine$AuthorityInfo r0 = (com.android.server.content.SyncStorageEngine.AuthorityInfo) r0
            r5 = 2
            java.lang.String r2 = "SyncManagerFile"
            boolean r18 = android.util.Log.isLoggable(r2, r5)
            java.lang.String r5 = " syncable="
            r19 = r12
            java.lang.String r12 = " enabled="
            if (r18 == 0) goto L_0x00d5
            r18 = r4
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r1 = "Adding authority: account="
            r4.append(r1)
            r4.append(r10)
            java.lang.String r1 = " accountType="
            r4.append(r1)
            r4.append(r11)
            java.lang.String r1 = " auth="
            r4.append(r1)
            r4.append(r8)
            java.lang.String r1 = " package="
            r4.append(r1)
            r4.append(r13)
            java.lang.String r1 = " class="
            r4.append(r1)
            r4.append(r7)
            java.lang.String r1 = " user="
            r4.append(r1)
            r4.append(r15)
            r4.append(r12)
            r4.append(r9)
            r4.append(r5)
            r4.append(r14)
            java.lang.String r1 = r4.toString()
            android.util.Slog.v(r2, r1)
            goto L_0x00d7
        L_0x00d5:
            r18 = r4
        L_0x00d7:
            if (r0 != 0) goto L_0x015d
            r4 = 2
            boolean r20 = android.util.Log.isLoggable(r2, r4)
            if (r20 == 0) goto L_0x00e5
            java.lang.String r4 = "Creating authority entry"
            android.util.Slog.v(r2, r4)
        L_0x00e5:
            if (r10 == 0) goto L_0x0154
            if (r8 == 0) goto L_0x0154
            com.android.server.content.SyncStorageEngine$EndPoint r2 = new com.android.server.content.SyncStorageEngine$EndPoint
            android.accounts.Account r4 = new android.accounts.Account
            r4.<init>(r10, r11)
            r2.<init>(r4, r8, r15)
            android.accounts.Account r4 = r2.account
            boolean r4 = r3.isAccountValid(r4, r15)
            if (r4 == 0) goto L_0x0118
            boolean r4 = r3.isAuthorityValid(r8, r15)
            if (r4 == 0) goto L_0x0115
            r1 = 0
            r4 = r23
            com.android.server.content.SyncStorageEngine$AuthorityInfo r0 = r4.getOrCreateAuthorityLocked(r2, r6, r1)
            if (r25 <= 0) goto L_0x010f
            java.util.ArrayList<android.content.PeriodicSync> r1 = r0.periodicSyncs
            r1.clear()
        L_0x010f:
            r1 = r0
            r16 = 0
            r20 = 1
            goto L_0x0167
        L_0x0115:
            r4 = r23
            goto L_0x011a
        L_0x0118:
            r4 = r23
        L_0x011a:
            r1 = 3
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r21 = "35028827"
            r16 = 0
            r1[r16] = r21
            r17 = -1
            java.lang.Integer r21 = java.lang.Integer.valueOf(r17)
            r20 = 1
            r1[r20] = r21
            r21 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r22 = r2
            java.lang.String r2 = "account:*** provider:"
            r0.append(r2)
            r0.append(r8)
            java.lang.String r2 = " user:"
            r0.append(r2)
            r0.append(r15)
            java.lang.String r0 = r0.toString()
            r2 = 2
            r1[r2] = r0
            r0 = 1397638484(0x534e4554, float:8.859264E11)
            android.util.EventLog.writeEvent(r0, r1)
            goto L_0x0165
        L_0x0154:
            r16 = 0
            r20 = 1
            r4 = r23
            r21 = r0
            goto L_0x0165
        L_0x015d:
            r16 = 0
            r20 = 1
            r4 = r23
            r21 = r0
        L_0x0165:
            r1 = r21
        L_0x0167:
            if (r1 == 0) goto L_0x01a0
            if (r9 == 0) goto L_0x0175
            boolean r0 = java.lang.Boolean.parseBoolean(r9)
            if (r0 == 0) goto L_0x0172
            goto L_0x0175
        L_0x0172:
            r0 = r16
            goto L_0x0177
        L_0x0175:
            r0 = r20
        L_0x0177:
            r1.enabled = r0
            if (r14 != 0) goto L_0x017d
            r0 = -1
            goto L_0x0181
        L_0x017d:
            int r0 = java.lang.Integer.parseInt(r14)     // Catch:{ NumberFormatException -> 0x0184 }
        L_0x0181:
            r1.syncable = r0     // Catch:{ NumberFormatException -> 0x0184 }
        L_0x0183:
            goto L_0x01c5
        L_0x0184:
            r0 = move-exception
            java.lang.String r2 = "unknown"
            boolean r2 = r2.equals(r14)
            if (r2 == 0) goto L_0x0192
            r2 = -1
            r1.syncable = r2
            goto L_0x0183
        L_0x0192:
            boolean r2 = java.lang.Boolean.parseBoolean(r14)
            if (r2 == 0) goto L_0x019b
            r2 = r20
            goto L_0x019d
        L_0x019b:
            r2 = r16
        L_0x019d:
            r1.syncable = r2
            goto L_0x0183
        L_0x01a0:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Failure adding authority: auth="
            r0.append(r2)
            r0.append(r8)
            r0.append(r12)
            r0.append(r9)
            r0.append(r5)
            r0.append(r14)
            java.lang.String r0 = r0.toString()
            r2 = r18
            android.util.Slog.w(r2, r0)
            goto L_0x01c5
        L_0x01c3:
            r4 = r1
            r1 = r5
        L_0x01c5:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.content.SyncStorageEngine.parseAuthority(org.xmlpull.v1.XmlPullParser, int, com.android.server.content.SyncStorageEngine$AccountAuthorityValidator):com.android.server.content.SyncStorageEngine$AuthorityInfo");
    }

    private PeriodicSync parsePeriodicSync(XmlPullParser parser, AuthorityInfo authorityInfo) {
        long flextime;
        XmlPullParser xmlPullParser = parser;
        AuthorityInfo authorityInfo2 = authorityInfo;
        Bundle extras = new Bundle();
        String periodValue = xmlPullParser.getAttributeValue((String) null, "period");
        String flexValue = xmlPullParser.getAttributeValue((String) null, "flex");
        try {
            long period = Long.parseLong(periodValue);
            try {
                flextime = Long.parseLong(flexValue);
            } catch (NumberFormatException e) {
                NumberFormatException numberFormatException = e;
                long flextime2 = calculateDefaultFlexTime(period);
                Slog.e("SyncManager", "Error formatting value parsed for periodic sync flex: " + flexValue + ", using default: " + flextime2);
                flextime = flextime2;
            } catch (NullPointerException e2) {
                NullPointerException nullPointerException = e2;
                long flextime3 = calculateDefaultFlexTime(period);
                Slog.d("SyncManager", "No flex time specified for this sync, using a default. period: " + period + " flex: " + flextime3);
                flextime = flextime3;
            }
            PeriodicSync periodicSync = new PeriodicSync(authorityInfo2.target.account, authorityInfo2.target.provider, extras, period, flextime);
            authorityInfo2.periodicSyncs.add(periodicSync);
            return periodicSync;
        } catch (NumberFormatException e3) {
            Slog.e("SyncManager", "error parsing the period of a periodic sync", e3);
            return null;
        } catch (NullPointerException e4) {
            Slog.e("SyncManager", "the period of a periodic sync is null", e4);
            return null;
        }
    }

    private void parseExtra(XmlPullParser parser, Bundle extras) {
        String name = parser.getAttributeValue((String) null, Settings.ATTR_NAME);
        String type = parser.getAttributeValue((String) null, DatabaseHelper.SoundModelContract.KEY_TYPE);
        String value1 = parser.getAttributeValue((String) null, "value1");
        String value2 = parser.getAttributeValue((String) null, "value2");
        try {
            if ("long".equals(type)) {
                extras.putLong(name, Long.parseLong(value1));
            } else if ("integer".equals(type)) {
                extras.putInt(name, Integer.parseInt(value1));
            } else if ("double".equals(type)) {
                extras.putDouble(name, Double.parseDouble(value1));
            } else if ("float".equals(type)) {
                extras.putFloat(name, Float.parseFloat(value1));
            } else if ("boolean".equals(type)) {
                extras.putBoolean(name, Boolean.parseBoolean(value1));
            } else if ("string".equals(type)) {
                extras.putString(name, value1);
            } else if ("account".equals(type)) {
                extras.putParcelable(name, new Account(value1, value2));
            }
        } catch (NumberFormatException e) {
            Slog.e("SyncManager", "error parsing bundle value", e);
        } catch (NullPointerException e2) {
            Slog.e("SyncManager", "error parsing bundle value", e2);
        }
    }

    private void writeAccountInfoLocked() {
        if (Log.isLoggable(TAG_FILE, 2)) {
            Slog.v(TAG_FILE, "Writing new " + this.mAccountInfoFile.getBaseFile());
        }
        FileOutputStream fos = null;
        try {
            fos = this.mAccountInfoFile.startWrite();
            XmlSerializer out = new FastXmlSerializer();
            out.setOutput(fos, StandardCharsets.UTF_8.name());
            out.startDocument((String) null, true);
            out.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            out.startTag((String) null, "accounts");
            out.attribute((String) null, "version", Integer.toString(3));
            out.attribute((String) null, XML_ATTR_NEXT_AUTHORITY_ID, Integer.toString(this.mNextAuthorityId));
            out.attribute((String) null, XML_ATTR_SYNC_RANDOM_OFFSET, Integer.toString(this.mSyncRandomOffset));
            int M = this.mMasterSyncAutomatically.size();
            for (int m = 0; m < M; m++) {
                int userId = this.mMasterSyncAutomatically.keyAt(m);
                out.startTag((String) null, XML_TAG_LISTEN_FOR_TICKLES);
                out.attribute((String) null, XML_ATTR_USER, Integer.toString(userId));
                out.attribute((String) null, XML_ATTR_ENABLED, Boolean.toString(this.mMasterSyncAutomatically.valueAt(m).booleanValue()));
                out.endTag((String) null, XML_TAG_LISTEN_FOR_TICKLES);
            }
            int N = this.mAuthorities.size();
            for (int i = 0; i < N; i++) {
                AuthorityInfo authority = this.mAuthorities.valueAt(i);
                EndPoint info = authority.target;
                out.startTag((String) null, "authority");
                out.attribute((String) null, "id", Integer.toString(authority.ident));
                out.attribute((String) null, XML_ATTR_USER, Integer.toString(info.userId));
                out.attribute((String) null, XML_ATTR_ENABLED, Boolean.toString(authority.enabled));
                out.attribute((String) null, "account", info.account.name);
                out.attribute((String) null, DatabaseHelper.SoundModelContract.KEY_TYPE, info.account.type);
                out.attribute((String) null, "authority", info.provider);
                out.attribute((String) null, "syncable", Integer.toString(authority.syncable));
                out.endTag((String) null, "authority");
            }
            out.endTag((String) null, "accounts");
            out.endDocument();
            this.mAccountInfoFile.finishWrite(fos);
        } catch (IOException e1) {
            Slog.w("SyncManager", "Error writing accounts", e1);
            if (fos != null) {
                this.mAccountInfoFile.failWrite(fos);
            }
        }
    }

    private void readStatusLocked() {
        if (Log.isLoggable(TAG_FILE, 2)) {
            Slog.v(TAG_FILE, "Reading " + this.mStatusFile.getBaseFile());
        }
        try {
            byte[] data = this.mStatusFile.readFully();
            Parcel in = Parcel.obtain();
            in.unmarshall(data, 0, data.length);
            in.setDataPosition(0);
            while (true) {
                int readInt = in.readInt();
                int token = readInt;
                if (readInt == 0) {
                    return;
                }
                if (token == 100) {
                    SyncStatusInfo status = new SyncStatusInfo(in);
                    if (this.mAuthorities.indexOfKey(status.authorityId) >= 0) {
                        status.pending = false;
                        if (Log.isLoggable(TAG_FILE, 2)) {
                            Slog.v(TAG_FILE, "Adding status for id " + status.authorityId);
                        }
                        this.mSyncStatus.put(status.authorityId, status);
                    }
                } else {
                    Slog.w("SyncManager", "Unknown status token: " + token);
                    return;
                }
            }
        } catch (IOException e) {
            Slog.i("SyncManager", "No initial status");
        } catch (RuntimeException ex) {
            Slog.wtf("SyncManager", "sync status file may be broken", ex);
            this.mSyncStatus.clear();
        }
    }

    /* access modifiers changed from: private */
    public void writeStatusLocked() {
        if (Log.isLoggable(TAG_FILE, 2)) {
            Slog.v(TAG_FILE, "Writing new " + this.mStatusFile.getBaseFile());
        }
        this.mHandler.removeMessages(1);
        try {
            FileOutputStream fos = this.mStatusFile.startWrite();
            Parcel out = Parcel.obtain();
            int N = this.mSyncStatus.size();
            for (int i = 0; i < N; i++) {
                out.writeInt(100);
                this.mSyncStatus.valueAt(i).writeToParcel(out, 0);
            }
            out.writeInt(0);
            fos.write(out.marshall());
            out.recycle();
            this.mStatusFile.finishWrite(fos);
        } catch (IOException e1) {
            Slog.w("SyncManager", "Error writing status", e1);
            if (0 != 0) {
                this.mStatusFile.failWrite((FileOutputStream) null);
            }
        }
    }

    private void requestSync(AuthorityInfo authorityInfo, int reason, Bundle extras, int syncExemptionFlag, int callingUid, int callingPid) {
        OnSyncRequestListener onSyncRequestListener;
        if (Process.myUid() != 1000 || (onSyncRequestListener = this.mSyncRequestListener) == null) {
            SyncRequest.Builder req = new SyncRequest.Builder().syncOnce().setExtras(extras);
            req.setSyncAdapter(authorityInfo.target.account, authorityInfo.target.provider);
            ContentResolver.requestSync(req.build());
            return;
        }
        onSyncRequestListener.onSyncRequest(authorityInfo.target, reason, extras, syncExemptionFlag, callingUid, callingPid);
    }

    private void requestSync(Account account, int userId, int reason, String authority, Bundle extras, int syncExemptionFlag, int callingUid, int callingPid) {
        Account account2 = account;
        String str = authority;
        if (Process.myUid() == 1000) {
            OnSyncRequestListener onSyncRequestListener = this.mSyncRequestListener;
            if (onSyncRequestListener != null) {
                int i = userId;
                onSyncRequestListener.onSyncRequest(new EndPoint(account, authority, userId), reason, extras, syncExemptionFlag, callingUid, callingPid);
                Bundle bundle = extras;
                return;
            }
        }
        int i2 = userId;
        ContentResolver.requestSync(account, authority, extras);
    }

    private void readStatisticsLocked() {
        try {
            byte[] data = this.mStatisticsFile.readFully();
            Parcel in = Parcel.obtain();
            in.unmarshall(data, 0, data.length);
            in.setDataPosition(0);
            int index = 0;
            while (true) {
                int readInt = in.readInt();
                int token = readInt;
                if (readInt != 0) {
                    if (token != 101) {
                        if (token != 100) {
                            Slog.w("SyncManager", "Unknown stats token: " + token);
                            return;
                        }
                    }
                    int day = in.readInt();
                    if (token == 100) {
                        day = (day - 2009) + 14245;
                    }
                    DayStats ds = new DayStats(day);
                    ds.successCount = in.readInt();
                    ds.successTime = in.readLong();
                    ds.failureCount = in.readInt();
                    ds.failureTime = in.readLong();
                    if (index < this.mDayStats.length) {
                        this.mDayStats[index] = ds;
                        index++;
                    }
                } else {
                    return;
                }
            }
        } catch (IOException e) {
            Slog.i("SyncManager", "No initial statistics");
        } catch (RuntimeException ex) {
            Slog.wtf("SyncManager", "stats file may be broken", ex);
        }
    }

    /* access modifiers changed from: private */
    public void writeStatisticsLocked() {
        if (Log.isLoggable(TAG_FILE, 2)) {
            Slog.v("SyncManager", "Writing new " + this.mStatisticsFile.getBaseFile());
        }
        this.mHandler.removeMessages(2);
        try {
            FileOutputStream fos = this.mStatisticsFile.startWrite();
            Parcel out = Parcel.obtain();
            int N = this.mDayStats.length;
            int i = 0;
            while (true) {
                if (i >= N) {
                    break;
                }
                DayStats ds = this.mDayStats[i];
                if (ds == null) {
                    break;
                }
                out.writeInt(101);
                out.writeInt(ds.day);
                out.writeInt(ds.successCount);
                out.writeLong(ds.successTime);
                out.writeInt(ds.failureCount);
                out.writeLong(ds.failureTime);
                i++;
            }
            out.writeInt(0);
            fos.write(out.marshall());
            out.recycle();
            this.mStatisticsFile.finishWrite(fos);
        } catch (IOException e1) {
            Slog.w("SyncManager", "Error writing stats", e1);
            if (0 != 0) {
                this.mStatisticsFile.failWrite((FileOutputStream) null);
            }
        }
    }

    public void queueBackup() {
        BackupManager.dataChanged(PackageManagerService.PLATFORM_PACKAGE_NAME);
    }

    public void setClockValid() {
        if (!this.mIsClockValid) {
            this.mIsClockValid = true;
            Slog.w("SyncManager", "Clock is valid now.");
        }
    }

    public boolean isClockValid() {
        return this.mIsClockValid;
    }

    public void resetTodayStats(boolean force) {
        if (force) {
            Log.w("SyncManager", "Force resetting today stats.");
        }
        synchronized (this.mAuthorities) {
            int N = this.mSyncStatus.size();
            for (int i = 0; i < N; i++) {
                this.mSyncStatus.valueAt(i).maybeResetTodayStats(isClockValid(), force);
            }
            writeStatusLocked();
        }
    }

    public void setMiSyncPauseToTime(Account account, long pauseTimeMillis, int uid) {
        synchronized (this.mAuthorities) {
            SyncStorageEngineInjector.setMiSyncPauseToTimeLocked(account, pauseTimeMillis, uid);
        }
        reportChange(1, uid);
    }

    public long getMiSyncPauseToTime(Account account, int uid) {
        long miSyncPauseToTimeLocked;
        synchronized (this.mAuthorities) {
            miSyncPauseToTimeLocked = SyncStorageEngineInjector.getMiSyncPauseToTimeLocked(account, uid);
        }
        return miSyncPauseToTimeLocked;
    }

    public void setMiSyncStrategy(Account account, int strategy, int uid) {
        synchronized (this.mAuthorities) {
            SyncStorageEngineInjector.setMiSyncStrategyLocked(account, strategy, uid);
        }
        reportChange(1, uid);
    }

    public int getMiSyncStrategy(Account account, int uid) {
        int miSyncStrategyLocked;
        synchronized (this.mAuthorities) {
            miSyncStrategyLocked = SyncStorageEngineInjector.getMiSyncStrategyLocked(account, uid);
        }
        return miSyncStrategyLocked;
    }

    /* access modifiers changed from: package-private */
    public MiSyncPause getMiSyncPause(String accountName, int uid) {
        MiSyncPause miSyncPauseLocked;
        synchronized (this.mAuthorities) {
            miSyncPauseLocked = SyncStorageEngineInjector.getMiSyncPauseLocked(accountName, uid);
        }
        return miSyncPauseLocked;
    }

    /* access modifiers changed from: package-private */
    public MiSyncStrategy getMiSyncStrategy(String accountName, int uid) {
        MiSyncStrategy miSyncStrategyLocked;
        synchronized (this.mAuthorities) {
            miSyncStrategyLocked = SyncStorageEngineInjector.getMiSyncStrategyLocked(accountName, uid);
        }
        return miSyncStrategyLocked;
    }
}
