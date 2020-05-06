package com.android.server.backup;

import android.app.ActivityManagerInternal;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.app.IBackupAgent;
import android.app.PendingIntent;
import android.app.backup.BackupAgent;
import android.app.backup.IBackupManager;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IBackupObserver;
import android.app.backup.IFullBackupRestoreObserver;
import android.app.backup.IRestoreObserver;
import android.app.backup.IRestoreSession;
import android.app.backup.ISelectBackupTransportCallback;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.WorkSource;
import android.os.storage.IStorageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.EventLog;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.AppWidgetBackupBridge;
import com.android.server.EventLogTags;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.backup.fullbackup.FullBackupEntry;
import com.android.server.backup.fullbackup.PerformFullTransportBackupTask;
import com.android.server.backup.internal.BackupHandler;
import com.android.server.backup.internal.ClearDataObserver;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.internal.Operation;
import com.android.server.backup.internal.PerformInitializeTask;
import com.android.server.backup.keyvalue.BackupRequest;
import com.android.server.backup.params.AdbBackupParams;
import com.android.server.backup.params.AdbParams;
import com.android.server.backup.params.AdbRestoreParams;
import com.android.server.backup.params.BackupParams;
import com.android.server.backup.params.ClearParams;
import com.android.server.backup.params.ClearRetryParams;
import com.android.server.backup.params.RestoreParams;
import com.android.server.backup.restore.ActiveRestoreSession;
import com.android.server.backup.restore.PerformUnifiedRestoreTask;
import com.android.server.backup.transport.TransportClient;
import com.android.server.backup.transport.TransportNotRegisteredException;
import com.android.server.backup.utils.AppBackupUtils;
import com.android.server.backup.utils.BackupManagerMonitorUtils;
import com.android.server.backup.utils.BackupObserverUtils;
import com.android.server.backup.utils.FileUtils;
import com.android.server.backup.utils.SparseArrayUtils;
import com.android.server.job.JobSchedulerShellCommand;
import com.google.android.collect.Sets;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.security.SecureRandom;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class UserBackupManagerService {
    public static final String BACKUP_FILE_HEADER_MAGIC = "ANDROID BACKUP\n";
    public static final int BACKUP_FILE_VERSION = 5;
    private static final String BACKUP_FINISHED_ACTION = "android.intent.action.BACKUP_FINISHED";
    private static final String BACKUP_FINISHED_PACKAGE_EXTRA = "packageName";
    public static final String BACKUP_MANIFEST_FILENAME = "_manifest";
    public static final int BACKUP_MANIFEST_VERSION = 1;
    public static final String BACKUP_METADATA_FILENAME = "_meta";
    public static final int BACKUP_METADATA_VERSION = 1;
    public static final int BACKUP_WIDGET_METADATA_TOKEN = 33549569;
    private static final long BIND_TIMEOUT_INTERVAL = 10000;
    private static final int BUSY_BACKOFF_FUZZ = 7200000;
    private static final long BUSY_BACKOFF_MIN_MILLIS = 3600000;
    private static final long CLEAR_DATA_TIMEOUT_INTERVAL = 30000;
    private static final int CURRENT_ANCESTRAL_RECORD_VERSION = 1;
    private static final long INITIALIZATION_DELAY_MILLIS = 3000;
    private static final String INIT_SENTINEL_FILE_NAME = "_need_init_";
    public static final String KEY_WIDGET_STATE = "￭￭widget";
    private static final int OP_ACKNOWLEDGED = 1;
    public static final int OP_PENDING = 0;
    private static final int OP_TIMEOUT = -1;
    public static final int OP_TYPE_BACKUP = 2;
    public static final int OP_TYPE_BACKUP_WAIT = 0;
    public static final int OP_TYPE_RESTORE_WAIT = 1;
    public static final String PACKAGE_MANAGER_SENTINEL = "@pm@";
    public static final String RUN_BACKUP_ACTION = "android.app.backup.intent.RUN";
    public static final String RUN_INITIALIZE_ACTION = "android.app.backup.intent.INIT";
    private static final int SCHEDULE_FILE_VERSION = 1;
    private static final String SERIAL_ID_FILE = "serial_id";
    public static final String SETTINGS_PACKAGE = "com.android.providers.settings";
    public static final String SHARED_BACKUP_AGENT_PACKAGE = "com.android.sharedstoragebackup";
    private static final long TIMEOUT_FULL_CONFIRMATION = 60000;
    private static final long TRANSPORT_RETRY_INTERVAL = 3600000;
    private ActiveRestoreSession mActiveRestoreSession;
    private final IActivityManager mActivityManager;
    private final ActivityManagerInternal mActivityManagerInternal;
    private final SparseArray<AdbParams> mAdbBackupRestoreConfirmations = new SparseArray<>();
    private final Object mAgentConnectLock = new Object();
    private final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    private final AlarmManager mAlarmManager;
    private Set<String> mAncestralPackages = null;
    private File mAncestralSerialNumberFile;
    private long mAncestralToken = 0;
    private boolean mAutoRestore;
    /* access modifiers changed from: private */
    public final BackupHandler mBackupHandler;
    private final IBackupManager mBackupManagerBinder;
    /* access modifiers changed from: private */
    public final SparseArray<HashSet<String>> mBackupParticipants = new SparseArray<>();
    private final BackupPasswordManager mBackupPasswordManager;
    private volatile boolean mBackupRunning;
    private final File mBaseStateDir;
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /* Debug info: failed to restart local var, previous not found, register: 17 */
        /* JADX WARNING: Code restructure failed: missing block: B:44:0x010e, code lost:
            r11 = java.lang.System.currentTimeMillis();
            r13 = r7.length;
            r14 = 0;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r18, android.content.Intent r19) {
            /*
                r17 = this;
                r1 = r17
                r2 = r19
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                r0.<init>()
                java.lang.String r3 = "Received broadcast "
                r0.append(r3)
                r0.append(r2)
                java.lang.String r0 = r0.toString()
                java.lang.String r3 = "BackupManagerService"
                android.util.Slog.d(r3, r0)
                java.lang.String r3 = r19.getAction()
                r0 = 0
                r4 = 0
                r5 = 0
                android.os.Bundle r6 = r19.getExtras()
                r7 = 0
                java.lang.String r8 = "android.intent.action.PACKAGE_ADDED"
                boolean r8 = r8.equals(r3)
                r9 = 0
                if (r8 != 0) goto L_0x006a
                java.lang.String r8 = "android.intent.action.PACKAGE_REMOVED"
                boolean r8 = r8.equals(r3)
                if (r8 != 0) goto L_0x006a
                java.lang.String r8 = "android.intent.action.PACKAGE_CHANGED"
                boolean r8 = r8.equals(r3)
                if (r8 == 0) goto L_0x0040
                goto L_0x006a
            L_0x0040:
                java.lang.String r8 = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE"
                boolean r8 = r8.equals(r3)
                if (r8 == 0) goto L_0x0053
                r4 = 1
                java.lang.String r8 = "android.intent.extra.changed_package_list"
                java.lang.String[] r7 = r2.getStringArrayExtra(r8)
                r8 = r5
                r5 = r0
                goto L_0x00e3
            L_0x0053:
                java.lang.String r8 = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE"
                boolean r8 = r8.equals(r3)
                if (r8 == 0) goto L_0x0066
                r4 = 0
                java.lang.String r8 = "android.intent.extra.changed_package_list"
                java.lang.String[] r7 = r2.getStringArrayExtra(r8)
                r8 = r5
                r5 = r0
                goto L_0x00e3
            L_0x0066:
                r8 = r5
                r5 = r0
                goto L_0x00e3
            L_0x006a:
                android.net.Uri r8 = r19.getData()
                if (r8 != 0) goto L_0x0071
                return
            L_0x0071:
                java.lang.String r10 = r8.getSchemeSpecificPart()
                if (r10 == 0) goto L_0x007d
                r11 = 1
                java.lang.String[] r11 = new java.lang.String[r11]
                r11[r9] = r10
                r7 = r11
            L_0x007d:
                java.lang.String r11 = "android.intent.action.PACKAGE_CHANGED"
                boolean r5 = r11.equals(r3)
                if (r5 == 0) goto L_0x00d5
                java.lang.String r9 = "android.intent.extra.changed_component_name_list"
                java.lang.String[] r9 = r2.getStringArrayExtra(r9)
                java.lang.StringBuilder r11 = new java.lang.StringBuilder
                r11.<init>()
                java.lang.String r12 = "Package "
                r11.append(r12)
                r11.append(r10)
                java.lang.String r12 = " changed"
                r11.append(r12)
                java.lang.String r11 = r11.toString()
                java.lang.String r12 = "BackupManagerService"
                android.util.Slog.i(r12, r11)
                r11 = 0
            L_0x00a8:
                int r12 = r9.length
                if (r11 >= r12) goto L_0x00c6
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                java.lang.String r13 = "   * "
                r12.append(r13)
                r13 = r9[r11]
                r12.append(r13)
                java.lang.String r12 = r12.toString()
                java.lang.String r13 = "BackupManagerService"
                android.util.Slog.i(r13, r12)
                int r11 = r11 + 1
                goto L_0x00a8
            L_0x00c6:
                com.android.server.backup.UserBackupManagerService r11 = com.android.server.backup.UserBackupManagerService.this
                com.android.server.backup.internal.BackupHandler r11 = r11.mBackupHandler
                com.android.server.backup.-$$Lambda$UserBackupManagerService$2$VpHOYQHCWBG618oharjEXEDr57U r12 = new com.android.server.backup.-$$Lambda$UserBackupManagerService$2$VpHOYQHCWBG618oharjEXEDr57U
                r12.<init>(r10, r9)
                r11.post(r12)
                return
            L_0x00d5:
                java.lang.String r11 = "android.intent.action.PACKAGE_ADDED"
                boolean r4 = r11.equals(r3)
                java.lang.String r11 = "android.intent.extra.REPLACING"
                boolean r0 = r6.getBoolean(r11, r9)
                r8 = r5
                r5 = r0
            L_0x00e3:
                if (r7 == 0) goto L_0x01d9
                int r0 = r7.length
                if (r0 != 0) goto L_0x00ec
                r16 = r3
                goto L_0x01db
            L_0x00ec:
                java.lang.String r0 = "android.intent.extra.UID"
                int r10 = r6.getInt(r0)
                if (r4 == 0) goto L_0x01ac
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this
                android.util.SparseArray r11 = r0.mBackupParticipants
                monitor-enter(r11)
                if (r5 == 0) goto L_0x0108
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ all -> 0x0103 }
                r0.removePackageParticipantsLocked(r7, r10)     // Catch:{ all -> 0x0103 }
                goto L_0x0108
            L_0x0103:
                r0 = move-exception
                r16 = r3
                goto L_0x01a8
            L_0x0108:
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ all -> 0x01a5 }
                r0.addPackageParticipantsLocked(r7)     // Catch:{ all -> 0x01a5 }
                monitor-exit(r11)     // Catch:{ all -> 0x01a5 }
                long r11 = java.lang.System.currentTimeMillis()
                int r13 = r7.length
                r14 = r9
            L_0x0114:
                if (r14 >= r13) goto L_0x019b
                r15 = r7[r14]
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ NameNotFoundException -> 0x0179 }
                android.content.pm.PackageManager r0 = r0.mPackageManager     // Catch:{ NameNotFoundException -> 0x0179 }
                com.android.server.backup.UserBackupManagerService r9 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ NameNotFoundException -> 0x0179 }
                int r9 = r9.mUserId     // Catch:{ NameNotFoundException -> 0x0179 }
                r2 = 0
                android.content.pm.PackageInfo r0 = r0.getPackageInfoAsUser(r15, r2, r9)     // Catch:{ NameNotFoundException -> 0x0179 }
                r9 = r0
                boolean r0 = com.android.server.backup.utils.AppBackupUtils.appGetsFullBackup(r9)     // Catch:{ NameNotFoundException -> 0x0179 }
                if (r0 == 0) goto L_0x0150
                android.content.pm.ApplicationInfo r0 = r9.applicationInfo     // Catch:{ NameNotFoundException -> 0x0179 }
                com.android.server.backup.UserBackupManagerService r2 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ NameNotFoundException -> 0x0179 }
                int r2 = r2.mUserId     // Catch:{ NameNotFoundException -> 0x0179 }
                boolean r0 = com.android.server.backup.utils.AppBackupUtils.appIsEligibleForBackup(r0, r2)     // Catch:{ NameNotFoundException -> 0x0179 }
                if (r0 == 0) goto L_0x014d
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ NameNotFoundException -> 0x0179 }
                r0.enqueueFullBackup(r15, r11)     // Catch:{ NameNotFoundException -> 0x0179 }
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ NameNotFoundException -> 0x0179 }
                r16 = r3
                r2 = 0
                r0.scheduleNextFullBackupJob(r2)     // Catch:{ NameNotFoundException -> 0x0177 }
                goto L_0x0164
            L_0x014d:
                r16 = r3
                goto L_0x0152
            L_0x0150:
                r16 = r3
            L_0x0152:
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ NameNotFoundException -> 0x0177 }
                java.lang.Object r2 = r0.mQueueLock     // Catch:{ NameNotFoundException -> 0x0177 }
                monitor-enter(r2)     // Catch:{ NameNotFoundException -> 0x0177 }
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ all -> 0x0174 }
                r0.dequeueFullBackupLocked(r15)     // Catch:{ all -> 0x0174 }
                monitor-exit(r2)     // Catch:{ all -> 0x0174 }
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ NameNotFoundException -> 0x0177 }
                r0.writeFullBackupScheduleAsync()     // Catch:{ NameNotFoundException -> 0x0177 }
            L_0x0164:
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ NameNotFoundException -> 0x0177 }
                com.android.server.backup.internal.BackupHandler r0 = r0.mBackupHandler     // Catch:{ NameNotFoundException -> 0x0177 }
                com.android.server.backup.-$$Lambda$UserBackupManagerService$2$9w65wn45YYtTkXbyQZdj_7K5LSs r2 = new com.android.server.backup.-$$Lambda$UserBackupManagerService$2$9w65wn45YYtTkXbyQZdj_7K5LSs     // Catch:{ NameNotFoundException -> 0x0177 }
                r2.<init>(r15)     // Catch:{ NameNotFoundException -> 0x0177 }
                r0.post(r2)     // Catch:{ NameNotFoundException -> 0x0177 }
                goto L_0x0192
            L_0x0174:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x0174 }
                throw r0     // Catch:{ NameNotFoundException -> 0x0177 }
            L_0x0177:
                r0 = move-exception
                goto L_0x017c
            L_0x0179:
                r0 = move-exception
                r16 = r3
            L_0x017c:
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "Can't resolve new app "
                r2.append(r3)
                r2.append(r15)
                java.lang.String r2 = r2.toString()
                java.lang.String r3 = "BackupManagerService"
                android.util.Slog.w(r3, r2)
            L_0x0192:
                int r14 = r14 + 1
                r2 = r19
                r3 = r16
                r9 = 0
                goto L_0x0114
            L_0x019b:
                r16 = r3
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this
                java.lang.String r2 = "@pm@"
                r0.dataChangedImpl(r2)
                goto L_0x01d8
            L_0x01a5:
                r0 = move-exception
                r16 = r3
            L_0x01a8:
                monitor-exit(r11)     // Catch:{ all -> 0x01aa }
                throw r0
            L_0x01aa:
                r0 = move-exception
                goto L_0x01a8
            L_0x01ac:
                r16 = r3
                if (r5 != 0) goto L_0x01c1
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this
                android.util.SparseArray r2 = r0.mBackupParticipants
                monitor-enter(r2)
                com.android.server.backup.UserBackupManagerService r0 = com.android.server.backup.UserBackupManagerService.this     // Catch:{ all -> 0x01be }
                r0.removePackageParticipantsLocked(r7, r10)     // Catch:{ all -> 0x01be }
                monitor-exit(r2)     // Catch:{ all -> 0x01be }
                goto L_0x01c1
            L_0x01be:
                r0 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x01be }
                throw r0
            L_0x01c1:
                int r0 = r7.length
                r2 = 0
            L_0x01c3:
                if (r2 >= r0) goto L_0x01d8
                r3 = r7[r2]
                com.android.server.backup.UserBackupManagerService r9 = com.android.server.backup.UserBackupManagerService.this
                com.android.server.backup.internal.BackupHandler r9 = r9.mBackupHandler
                com.android.server.backup.-$$Lambda$UserBackupManagerService$2$ICUfBQAK1UQkmGSsPDmR00etFBk r11 = new com.android.server.backup.-$$Lambda$UserBackupManagerService$2$ICUfBQAK1UQkmGSsPDmR00etFBk
                r11.<init>(r3)
                r9.post(r11)
                int r2 = r2 + 1
                goto L_0x01c3
            L_0x01d8:
                return
            L_0x01d9:
                r16 = r3
            L_0x01db:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.AnonymousClass2.onReceive(android.content.Context, android.content.Intent):void");
        }

        public /* synthetic */ void lambda$onReceive$0$UserBackupManagerService$2(String packageName, String[] components) {
            UserBackupManagerService.this.mTransportManager.onPackageChanged(packageName, components);
        }

        public /* synthetic */ void lambda$onReceive$1$UserBackupManagerService$2(String packageName) {
            UserBackupManagerService.this.mTransportManager.onPackageAdded(packageName);
        }

        public /* synthetic */ void lambda$onReceive$2$UserBackupManagerService$2(String packageName) {
            UserBackupManagerService.this.mTransportManager.onPackageRemoved(packageName);
        }
    };
    private int mCallerFd;
    private final Object mClearDataLock = new Object();
    private volatile boolean mClearingData;
    private IBackupAgent mConnectedAgent;
    private volatile boolean mConnecting;
    private final BackupManagerConstants mConstants;
    private final Context mContext;
    private final Object mCurrentOpLock = new Object();
    @GuardedBy({"mCurrentOpLock"})
    private final SparseArray<Operation> mCurrentOperations = new SparseArray<>();
    private long mCurrentToken = 0;
    private final File mDataDir;
    private boolean mEnabled;
    /* access modifiers changed from: private */
    @GuardedBy({"mQueueLock"})
    public ArrayList<FullBackupEntry> mFullBackupQueue;
    /* access modifiers changed from: private */
    public final File mFullBackupScheduleFile;
    private Runnable mFullBackupScheduleWriter = new Runnable() {
        public void run() {
            synchronized (UserBackupManagerService.this.mQueueLock) {
                try {
                    ByteArrayOutputStream bufStream = new ByteArrayOutputStream(4096);
                    DataOutputStream bufOut = new DataOutputStream(bufStream);
                    bufOut.writeInt(1);
                    int numPackages = UserBackupManagerService.this.mFullBackupQueue.size();
                    bufOut.writeInt(numPackages);
                    for (int i = 0; i < numPackages; i++) {
                        FullBackupEntry entry = (FullBackupEntry) UserBackupManagerService.this.mFullBackupQueue.get(i);
                        bufOut.writeUTF(entry.packageName);
                        bufOut.writeLong(entry.lastBackup);
                    }
                    bufOut.flush();
                    AtomicFile af = new AtomicFile(UserBackupManagerService.this.mFullBackupScheduleFile);
                    FileOutputStream out = af.startWrite();
                    out.write(bufStream.toByteArray());
                    af.finishWrite(out);
                } catch (Exception e) {
                    Slog.e(BackupManagerService.TAG, "Unable to write backup schedule!", e);
                }
            }
        }
    };
    @GuardedBy({"mPendingRestores"})
    private boolean mIsRestoreInProgress;
    private DataChangedJournal mJournal;
    private final File mJournalDir;
    private volatile long mLastBackupPass;
    private final AtomicInteger mNextToken = new AtomicInteger();
    /* access modifiers changed from: private */
    public final PackageManager mPackageManager;
    private final IPackageManager mPackageManagerBinder;
    private final HashMap<String, BackupRequest> mPendingBackups = new HashMap<>();
    private final ArraySet<String> mPendingInits = new ArraySet<>();
    @GuardedBy({"mPendingRestores"})
    private final Queue<PerformUnifiedRestoreTask> mPendingRestores = new ArrayDeque();
    private PowerManager mPowerManager;
    private ProcessedPackagesJournal mProcessedPackagesJournal;
    /* access modifiers changed from: private */
    public final Object mQueueLock = new Object();
    private final long mRegisterTransportsRequestedTime;
    private final SecureRandom mRng = new SecureRandom();
    private final PendingIntent mRunBackupIntent;
    private final PendingIntent mRunInitIntent;
    /* access modifiers changed from: private */
    @GuardedBy({"mQueueLock"})
    public PerformFullTransportBackupTask mRunningFullBackupTask;
    private boolean mSetupComplete;
    private final IStorageManager mStorageManager;
    private File mTokenFile;
    private final Random mTokenGenerator = new Random();
    /* access modifiers changed from: private */
    public final TransportManager mTransportManager;
    private final HandlerThread mUserBackupThread;
    /* access modifiers changed from: private */
    public final int mUserId;
    private final PowerManager.WakeLock mWakelock;

    static UserBackupManagerService createAndInitializeService(int userId, Context context, Trampoline trampoline, Set<ComponentName> transportWhitelist) {
        String currentTransport = Settings.Secure.getStringForUser(context.getContentResolver(), "backup_transport", userId);
        if (TextUtils.isEmpty(currentTransport)) {
            currentTransport = null;
        }
        Slog.v(BackupManagerService.TAG, "Starting with transport " + currentTransport);
        TransportManager transportManager = new TransportManager(userId, context, transportWhitelist, currentTransport);
        File baseStateDir = UserBackupManagerFiles.getBaseStateDir(userId);
        File dataDir = UserBackupManagerFiles.getDataDir(userId);
        HandlerThread userBackupThread = new HandlerThread("backup-" + userId, 10);
        userBackupThread.start();
        Slog.d(BackupManagerService.TAG, "Started thread " + userBackupThread.getName() + " for user " + userId);
        return createAndInitializeService(userId, context, trampoline, userBackupThread, baseStateDir, dataDir, transportManager);
    }

    @VisibleForTesting
    public static UserBackupManagerService createAndInitializeService(int userId, Context context, Trampoline trampoline, HandlerThread userBackupThread, File baseStateDir, File dataDir, TransportManager transportManager) {
        return new UserBackupManagerService(userId, context, trampoline, userBackupThread, baseStateDir, dataDir, transportManager);
    }

    public static boolean getSetupCompleteSettingForUser(Context context, int userId) {
        return Settings.Secure.getIntForUser(context.getContentResolver(), "user_setup_complete", 0, userId) != 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:21:0x028d, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private UserBackupManagerService(int r20, android.content.Context r21, com.android.server.backup.Trampoline r22, android.os.HandlerThread r23, java.io.File r24, java.io.File r25, com.android.server.backup.TransportManager r26) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            r11 = r21
            r12 = r23
            r19.<init>()
            android.util.ArraySet r0 = new android.util.ArraySet
            r0.<init>()
            r1.mPendingInits = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r1.mBackupParticipants = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r1.mPendingBackups = r0
            java.lang.Object r0 = new java.lang.Object
            r0.<init>()
            r1.mQueueLock = r0
            java.lang.Object r0 = new java.lang.Object
            r0.<init>()
            r1.mAgentConnectLock = r0
            java.lang.Object r0 = new java.lang.Object
            r0.<init>()
            r1.mClearDataLock = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r1.mAdbBackupRestoreConfirmations = r0
            java.security.SecureRandom r0 = new java.security.SecureRandom
            r0.<init>()
            r1.mRng = r0
            java.util.ArrayDeque r0 = new java.util.ArrayDeque
            r0.<init>()
            r1.mPendingRestores = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r1.mCurrentOperations = r0
            java.lang.Object r0 = new java.lang.Object
            r0.<init>()
            r1.mCurrentOpLock = r0
            java.util.Random r0 = new java.util.Random
            r0.<init>()
            r1.mTokenGenerator = r0
            java.util.concurrent.atomic.AtomicInteger r0 = new java.util.concurrent.atomic.AtomicInteger
            r0.<init>()
            r1.mNextToken = r0
            r0 = 0
            r1.mAncestralPackages = r0
            r3 = 0
            r1.mAncestralToken = r3
            r1.mCurrentToken = r3
            com.android.server.backup.UserBackupManagerService$1 r3 = new com.android.server.backup.UserBackupManagerService$1
            r3.<init>()
            r1.mFullBackupScheduleWriter = r3
            com.android.server.backup.UserBackupManagerService$2 r3 = new com.android.server.backup.UserBackupManagerService$2
            r3.<init>()
            r1.mBroadcastReceiver = r3
            r1.mUserId = r2
            java.lang.String r3 = "context cannot be null"
            java.lang.Object r3 = com.android.internal.util.Preconditions.checkNotNull(r11, r3)
            android.content.Context r3 = (android.content.Context) r3
            r1.mContext = r3
            android.content.pm.PackageManager r3 = r21.getPackageManager()
            r1.mPackageManager = r3
            android.content.pm.IPackageManager r3 = android.app.AppGlobals.getPackageManager()
            r1.mPackageManagerBinder = r3
            android.app.IActivityManager r3 = android.app.ActivityManager.getService()
            r1.mActivityManager = r3
            java.lang.Class<android.app.ActivityManagerInternal> r3 = android.app.ActivityManagerInternal.class
            java.lang.Object r3 = com.android.server.LocalServices.getService(r3)
            android.app.ActivityManagerInternal r3 = (android.app.ActivityManagerInternal) r3
            r1.mActivityManagerInternal = r3
            java.lang.String r3 = "alarm"
            java.lang.Object r3 = r11.getSystemService(r3)
            android.app.AlarmManager r3 = (android.app.AlarmManager) r3
            r1.mAlarmManager = r3
            java.lang.String r3 = "power"
            java.lang.Object r3 = r11.getSystemService(r3)
            android.os.PowerManager r3 = (android.os.PowerManager) r3
            r1.mPowerManager = r3
            java.lang.String r3 = "mount"
            android.os.IBinder r3 = android.os.ServiceManager.getService(r3)
            android.os.storage.IStorageManager r3 = android.os.storage.IStorageManager.Stub.asInterface(r3)
            r1.mStorageManager = r3
            java.lang.String r3 = "trampoline cannot be null"
            r13 = r22
            com.android.internal.util.Preconditions.checkNotNull(r13, r3)
            android.os.IBinder r3 = r22.asBinder()
            android.app.backup.IBackupManager r3 = com.android.server.backup.Trampoline.asInterface(r3)
            r1.mBackupManagerBinder = r3
            com.android.server.backup.BackupAgentTimeoutParameters r3 = new com.android.server.backup.BackupAgentTimeoutParameters
            android.os.Handler r4 = android.os.Handler.getMain()
            android.content.Context r5 = r1.mContext
            android.content.ContentResolver r5 = r5.getContentResolver()
            r3.<init>(r4, r5)
            r1.mAgentTimeoutParameters = r3
            com.android.server.backup.BackupAgentTimeoutParameters r3 = r1.mAgentTimeoutParameters
            r3.start()
            java.lang.String r3 = "userBackupThread cannot be null"
            com.android.internal.util.Preconditions.checkNotNull(r12, r3)
            r1.mUserBackupThread = r12
            com.android.server.backup.internal.BackupHandler r3 = new com.android.server.backup.internal.BackupHandler
            android.os.Looper r4 = r23.getLooper()
            r3.<init>(r1, r4)
            r1.mBackupHandler = r3
            android.content.ContentResolver r14 = r21.getContentResolver()
            boolean r3 = getSetupCompleteSettingForUser(r11, r2)
            r1.mSetupComplete = r3
            r15 = 1
            java.lang.String r3 = "backup_auto_restore"
            int r3 = android.provider.Settings.Secure.getIntForUser(r14, r3, r15, r2)
            r10 = 0
            if (r3 == 0) goto L_0x0118
            r3 = r15
            goto L_0x0119
        L_0x0118:
            r3 = r10
        L_0x0119:
            r1.mAutoRestore = r3
            com.android.server.backup.internal.SetupObserver r3 = new com.android.server.backup.internal.SetupObserver
            com.android.server.backup.internal.BackupHandler r4 = r1.mBackupHandler
            r3.<init>(r1, r4)
            r9 = r3
            java.lang.String r3 = "user_setup_complete"
            android.net.Uri r3 = android.provider.Settings.Secure.getUriFor(r3)
            int r4 = r1.mUserId
            r14.registerContentObserver(r3, r10, r9, r4)
            java.lang.String r3 = "baseStateDir cannot be null"
            r8 = r24
            java.lang.Object r3 = com.android.internal.util.Preconditions.checkNotNull(r8, r3)
            java.io.File r3 = (java.io.File) r3
            r1.mBaseStateDir = r3
            if (r2 != 0) goto L_0x0163
            java.io.File r3 = r1.mBaseStateDir
            r3.mkdirs()
            java.io.File r3 = r1.mBaseStateDir
            boolean r3 = android.os.SELinux.restorecon(r3)
            if (r3 != 0) goto L_0x0163
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "SELinux restorecon failed on "
            r3.append(r4)
            java.io.File r4 = r1.mBaseStateDir
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "BackupManagerService"
            android.util.Slog.w(r4, r3)
        L_0x0163:
            java.lang.String r3 = "dataDir cannot be null"
            r7 = r25
            java.lang.Object r3 = com.android.internal.util.Preconditions.checkNotNull(r7, r3)
            java.io.File r3 = (java.io.File) r3
            r1.mDataDir = r3
            com.android.server.backup.BackupPasswordManager r3 = new com.android.server.backup.BackupPasswordManager
            android.content.Context r4 = r1.mContext
            java.io.File r5 = r1.mBaseStateDir
            java.security.SecureRandom r6 = r1.mRng
            r3.<init>(r4, r5, r6)
            r1.mBackupPasswordManager = r3
            com.android.server.backup.internal.RunBackupReceiver r4 = new com.android.server.backup.internal.RunBackupReceiver
            r4.<init>(r1)
            android.content.IntentFilter r3 = new android.content.IntentFilter
            r3.<init>()
            r6 = r3
            java.lang.String r3 = "android.app.backup.intent.RUN"
            r6.addAction(r3)
            android.os.UserHandle r5 = android.os.UserHandle.of(r20)
            r16 = 0
            java.lang.String r17 = "android.permission.BACKUP"
            r3 = r21
            r18 = r6
            r7 = r17
            r8 = r16
            r3.registerReceiverAsUser(r4, r5, r6, r7, r8)
            com.android.server.backup.internal.RunInitializeReceiver r6 = new com.android.server.backup.internal.RunInitializeReceiver
            r6.<init>(r1)
            android.content.IntentFilter r3 = new android.content.IntentFilter
            r3.<init>()
            java.lang.String r5 = "android.app.backup.intent.INIT"
            r3.addAction(r5)
            android.os.UserHandle r7 = android.os.UserHandle.of(r20)
            java.lang.String r17 = "android.permission.BACKUP"
            r5 = r21
            r8 = r3
            r18 = r9
            r9 = r17
            r15 = r10
            r10 = r16
            r5.registerReceiverAsUser(r6, r7, r8, r9, r10)
            android.content.Intent r5 = new android.content.Intent
            java.lang.String r7 = "android.app.backup.intent.RUN"
            r5.<init>(r7)
            r7 = 1073741824(0x40000000, float:2.0)
            r5.addFlags(r7)
            android.os.UserHandle r8 = android.os.UserHandle.of(r20)
            android.app.PendingIntent r8 = android.app.PendingIntent.getBroadcastAsUser(r11, r15, r5, r15, r8)
            r1.mRunBackupIntent = r8
            android.content.Intent r8 = new android.content.Intent
            java.lang.String r9 = "android.app.backup.intent.INIT"
            r8.<init>(r9)
            r8.addFlags(r7)
            android.os.UserHandle r7 = android.os.UserHandle.of(r20)
            android.app.PendingIntent r7 = android.app.PendingIntent.getBroadcastAsUser(r11, r15, r8, r15, r7)
            r1.mRunInitIntent = r7
            java.io.File r7 = new java.io.File
            java.io.File r9 = r1.mBaseStateDir
            java.lang.String r10 = "pending"
            r7.<init>(r9, r10)
            r1.mJournalDir = r7
            java.io.File r7 = r1.mJournalDir
            r7.mkdirs()
            r1.mJournal = r0
            com.android.server.backup.BackupManagerConstants r7 = new com.android.server.backup.BackupManagerConstants
            com.android.server.backup.internal.BackupHandler r9 = r1.mBackupHandler
            android.content.Context r10 = r1.mContext
            android.content.ContentResolver r10 = r10.getContentResolver()
            r7.<init>(r9, r10)
            r1.mConstants = r7
            com.android.server.backup.BackupManagerConstants r7 = r1.mConstants
            r7.start()
            android.util.SparseArray<java.util.HashSet<java.lang.String>> r7 = r1.mBackupParticipants
            monitor-enter(r7)
            r1.addPackageParticipantsLocked(r0)     // Catch:{ all -> 0x0283 }
            monitor-exit(r7)     // Catch:{ all -> 0x0283 }
            java.lang.String r0 = "transportManager cannot be null"
            r9 = r26
            java.lang.Object r0 = com.android.internal.util.Preconditions.checkNotNull(r9, r0)
            com.android.server.backup.TransportManager r0 = (com.android.server.backup.TransportManager) r0
            r1.mTransportManager = r0
            com.android.server.backup.TransportManager r0 = r1.mTransportManager
            com.android.server.backup.-$$Lambda$UserBackupManagerService$9cuIH_XloqtNByp_6hXeGaVars8 r7 = new com.android.server.backup.-$$Lambda$UserBackupManagerService$9cuIH_XloqtNByp_6hXeGaVars8
            r7.<init>()
            r0.setOnTransportRegisteredListener(r7)
            r15 = r3
            r10 = r4
            long r3 = android.os.SystemClock.elapsedRealtime()
            r1.mRegisterTransportsRequestedTime = r3
            com.android.server.backup.internal.BackupHandler r0 = r1.mBackupHandler
            com.android.server.backup.TransportManager r3 = r1.mTransportManager
            java.util.Objects.requireNonNull(r3)
            com.android.server.backup.-$$Lambda$pM_c5tVAGDtxjxLF_ONtACWWq6Q r4 = new com.android.server.backup.-$$Lambda$pM_c5tVAGDtxjxLF_ONtACWWq6Q
            r4.<init>()
            r16 = r5
            r3 = r6
            r5 = 3000(0xbb8, double:1.482E-320)
            r0.postDelayed(r4, r5)
            com.android.server.backup.internal.BackupHandler r0 = r1.mBackupHandler
            com.android.server.backup.-$$Lambda$UserBackupManagerService$_gNqJq9Ygtc0ZVwYhCSDKCUKrKY r4 = new com.android.server.backup.-$$Lambda$UserBackupManagerService$_gNqJq9Ygtc0ZVwYhCSDKCUKrKY
            r4.<init>()
            r0.postDelayed(r4, r5)
            android.os.PowerManager r0 = r1.mPowerManager
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "*backup*-"
            r4.append(r5)
            r4.append(r2)
            java.lang.String r4 = r4.toString()
            r5 = 1
            android.os.PowerManager$WakeLock r0 = r0.newWakeLock(r5, r4)
            r1.mWakelock = r0
            java.io.File r0 = new java.io.File
            java.io.File r4 = r1.mBaseStateDir
            java.lang.String r5 = "fb-schedule"
            r0.<init>(r4, r5)
            r1.mFullBackupScheduleFile = r0
            r19.initPackageTracking()
            return
        L_0x0283:
            r0 = move-exception
            r9 = r26
            r15 = r3
            r10 = r4
            r16 = r5
            r3 = r6
        L_0x028b:
            monitor-exit(r7)     // Catch:{ all -> 0x028d }
            throw r0
        L_0x028d:
            r0 = move-exception
            goto L_0x028b
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.<init>(int, android.content.Context, com.android.server.backup.Trampoline, android.os.HandlerThread, java.io.File, java.io.File, com.android.server.backup.TransportManager):void");
    }

    /* access modifiers changed from: package-private */
    public void initializeBackupEnableState() {
        setBackupEnabled(UserBackupManagerFilePersistedSettings.readBackupEnableState(this.mUserId));
    }

    /* access modifiers changed from: package-private */
    public void tearDownService() {
        this.mUserBackupThread.quit();
    }

    public int getUserId() {
        return this.mUserId;
    }

    public BackupManagerConstants getConstants() {
        return this.mConstants;
    }

    public BackupAgentTimeoutParameters getAgentTimeoutParameters() {
        return this.mAgentTimeoutParameters;
    }

    public Context getContext() {
        return this.mContext;
    }

    public PackageManager getPackageManager() {
        return this.mPackageManager;
    }

    public IPackageManager getPackageManagerBinder() {
        return this.mPackageManagerBinder;
    }

    public IActivityManager getActivityManager() {
        return this.mActivityManager;
    }

    public AlarmManager getAlarmManager() {
        return this.mAlarmManager;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setPowerManager(PowerManager powerManager) {
        this.mPowerManager = powerManager;
    }

    public TransportManager getTransportManager() {
        return this.mTransportManager;
    }

    public boolean isEnabled() {
        return this.mEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.mEnabled = enabled;
    }

    public boolean isSetupComplete() {
        return this.mSetupComplete;
    }

    public void setSetupComplete(boolean setupComplete) {
        this.mSetupComplete = setupComplete;
    }

    public PowerManager.WakeLock getWakelock() {
        return this.mWakelock;
    }

    @VisibleForTesting
    public void setWorkSource(WorkSource workSource) {
        this.mWakelock.setWorkSource(workSource);
    }

    public Handler getBackupHandler() {
        return this.mBackupHandler;
    }

    public PendingIntent getRunInitIntent() {
        return this.mRunInitIntent;
    }

    public HashMap<String, BackupRequest> getPendingBackups() {
        return this.mPendingBackups;
    }

    public Object getQueueLock() {
        return this.mQueueLock;
    }

    public boolean isBackupRunning() {
        return this.mBackupRunning;
    }

    public void setBackupRunning(boolean backupRunning) {
        this.mBackupRunning = backupRunning;
    }

    public void setLastBackupPass(long lastBackupPass) {
        this.mLastBackupPass = lastBackupPass;
    }

    public Object getClearDataLock() {
        return this.mClearDataLock;
    }

    public void setClearingData(boolean clearingData) {
        this.mClearingData = clearingData;
    }

    public boolean isRestoreInProgress() {
        return this.mIsRestoreInProgress;
    }

    public void setRestoreInProgress(boolean restoreInProgress) {
        this.mIsRestoreInProgress = restoreInProgress;
    }

    public Queue<PerformUnifiedRestoreTask> getPendingRestores() {
        return this.mPendingRestores;
    }

    public ActiveRestoreSession getActiveRestoreSession() {
        return this.mActiveRestoreSession;
    }

    public SparseArray<Operation> getCurrentOperations() {
        return this.mCurrentOperations;
    }

    public Object getCurrentOpLock() {
        return this.mCurrentOpLock;
    }

    public SparseArray<AdbParams> getAdbBackupRestoreConfirmations() {
        return this.mAdbBackupRestoreConfirmations;
    }

    public File getBaseStateDir() {
        return this.mBaseStateDir;
    }

    public File getDataDir() {
        return this.mDataDir;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public BroadcastReceiver getPackageTrackingReceiver() {
        return this.mBroadcastReceiver;
    }

    public DataChangedJournal getJournal() {
        return this.mJournal;
    }

    public void setJournal(DataChangedJournal journal) {
        this.mJournal = journal;
    }

    public SecureRandom getRng() {
        return this.mRng;
    }

    public void setAncestralPackages(Set<String> ancestralPackages) {
        this.mAncestralPackages = ancestralPackages;
    }

    public void setAncestralToken(long ancestralToken) {
        this.mAncestralToken = ancestralToken;
    }

    public long getCurrentToken() {
        return this.mCurrentToken;
    }

    public void setCurrentToken(long currentToken) {
        this.mCurrentToken = currentToken;
    }

    public ArraySet<String> getPendingInits() {
        return this.mPendingInits;
    }

    public void clearPendingInits() {
        this.mPendingInits.clear();
    }

    public PerformFullTransportBackupTask getRunningFullBackupTask() {
        return this.mRunningFullBackupTask;
    }

    public void setRunningFullBackupTask(PerformFullTransportBackupTask runningFullBackupTask) {
        this.mRunningFullBackupTask = runningFullBackupTask;
    }

    public int generateRandomIntegerToken() {
        int token = this.mTokenGenerator.nextInt();
        if (token < 0) {
            token = -token;
        }
        return (token & -256) | (this.mNextToken.incrementAndGet() & 255);
    }

    public BackupAgent makeMetadataAgent() {
        PackageManagerBackupAgent pmAgent = new PackageManagerBackupAgent(this.mPackageManager, this.mUserId);
        pmAgent.attach(this.mContext);
        pmAgent.onCreate(UserHandle.of(this.mUserId));
        return pmAgent;
    }

    public PackageManagerBackupAgent makeMetadataAgent(List<PackageInfo> packages) {
        PackageManagerBackupAgent pmAgent = new PackageManagerBackupAgent(this.mPackageManager, packages, this.mUserId);
        pmAgent.attach(this.mContext);
        pmAgent.onCreate(UserHandle.of(this.mUserId));
        return pmAgent;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x005a, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005e, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void initPackageTracking() {
        /*
            r8 = this;
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r1 = "` tracking"
            android.util.Slog.v(r0, r1)
            java.io.File r0 = new java.io.File
            java.io.File r1 = r8.mBaseStateDir
            java.lang.String r2 = "ancestral"
            r0.<init>(r1, r2)
            r8.mTokenFile = r0
            java.io.DataInputStream r0 = new java.io.DataInputStream     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            java.io.BufferedInputStream r1 = new java.io.BufferedInputStream     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            java.io.File r3 = r8.mTokenFile     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            r2.<init>(r3)     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            r1.<init>(r2)     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            r0.<init>(r1)     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            r1 = 0
            int r2 = r0.readInt()     // Catch:{ all -> 0x0058 }
            r3 = 1
            if (r2 != r3) goto L_0x0054
            long r3 = r0.readLong()     // Catch:{ all -> 0x0058 }
            r8.mAncestralToken = r3     // Catch:{ all -> 0x0058 }
            long r3 = r0.readLong()     // Catch:{ all -> 0x0058 }
            r8.mCurrentToken = r3     // Catch:{ all -> 0x0058 }
            int r3 = r0.readInt()     // Catch:{ all -> 0x0058 }
            if (r3 < 0) goto L_0x0054
            java.util.HashSet r4 = new java.util.HashSet     // Catch:{ all -> 0x0058 }
            r4.<init>()     // Catch:{ all -> 0x0058 }
            r8.mAncestralPackages = r4     // Catch:{ all -> 0x0058 }
            r4 = 0
        L_0x0045:
            if (r4 >= r3) goto L_0x0054
            java.lang.String r5 = r0.readUTF()     // Catch:{ all -> 0x0058 }
            java.util.Set<java.lang.String> r6 = r8.mAncestralPackages     // Catch:{ all -> 0x0058 }
            r6.add(r5)     // Catch:{ all -> 0x0058 }
            int r4 = r4 + 1
            goto L_0x0045
        L_0x0054:
            $closeResource(r1, r0)     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            goto L_0x0070
        L_0x0058:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x005a }
        L_0x005a:
            r2 = move-exception
            $closeResource(r1, r0)     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
            throw r2     // Catch:{ FileNotFoundException -> 0x0068, IOException -> 0x005f }
        L_0x005f:
            r0 = move-exception
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r2 = "Unable to read token file"
            android.util.Slog.w(r1, r2, r0)
            goto L_0x0071
        L_0x0068:
            r0 = move-exception
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r2 = "No ancestral data"
            android.util.Slog.v(r1, r2)
        L_0x0070:
        L_0x0071:
            com.android.server.backup.ProcessedPackagesJournal r0 = new com.android.server.backup.ProcessedPackagesJournal
            java.io.File r1 = r8.mBaseStateDir
            r0.<init>(r1)
            r8.mProcessedPackagesJournal = r0
            com.android.server.backup.ProcessedPackagesJournal r0 = r8.mProcessedPackagesJournal
            r0.init()
            java.lang.Object r0 = r8.mQueueLock
            monitor-enter(r0)
            java.util.ArrayList r1 = r8.readFullBackupSchedule()     // Catch:{ all -> 0x00d2 }
            r8.mFullBackupQueue = r1     // Catch:{ all -> 0x00d2 }
            monitor-exit(r0)     // Catch:{ all -> 0x00d2 }
            android.content.IntentFilter r0 = new android.content.IntentFilter
            r0.<init>()
            java.lang.String r1 = "android.intent.action.PACKAGE_ADDED"
            r0.addAction(r1)
            java.lang.String r1 = "android.intent.action.PACKAGE_REMOVED"
            r0.addAction(r1)
            java.lang.String r1 = "android.intent.action.PACKAGE_CHANGED"
            r0.addAction(r1)
            java.lang.String r1 = "package"
            r0.addDataScheme(r1)
            android.content.Context r1 = r8.mContext
            android.content.BroadcastReceiver r2 = r8.mBroadcastReceiver
            int r3 = r8.mUserId
            android.os.UserHandle r3 = android.os.UserHandle.of(r3)
            r5 = 0
            r6 = 0
            r4 = r0
            r1.registerReceiverAsUser(r2, r3, r4, r5, r6)
            android.content.IntentFilter r1 = new android.content.IntentFilter
            r1.<init>()
            java.lang.String r2 = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE"
            r1.addAction(r2)
            java.lang.String r2 = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE"
            r1.addAction(r2)
            android.content.Context r2 = r8.mContext
            android.content.BroadcastReceiver r3 = r8.mBroadcastReceiver
            int r4 = r8.mUserId
            android.os.UserHandle r4 = android.os.UserHandle.of(r4)
            r7 = 0
            r5 = r1
            r2.registerReceiverAsUser(r3, r4, r5, r6, r7)
            return
        L_0x00d2:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00d2 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.initPackageTracking():void");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x01cc, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x01cd, code lost:
        r8 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:?, code lost:
        $closeResource(r2, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x01d1, code lost:
        throw r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x01d2, code lost:
        r0 = e;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:21:0x0052, B:130:0x01cb] */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x01e7  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0224  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.util.ArrayList<com.android.server.backup.fullbackup.FullBackupEntry> readFullBackupSchedule() {
        /*
            r22 = this;
            r1 = r22
            java.lang.String r2 = "Package "
            java.lang.String r3 = "BackupManagerService"
            r4 = 0
            r5 = 0
            android.content.pm.PackageManager r0 = r1.mPackageManager
            int r6 = r1.mUserId
            java.util.List r6 = com.android.server.backup.PackageManagerBackupAgent.getStorableApplications(r0, r6)
            java.io.File r0 = r1.mFullBackupScheduleFile
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x01e3
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x01d4 }
            java.io.File r9 = r1.mFullBackupScheduleFile     // Catch:{ Exception -> 0x01d4 }
            r0.<init>(r9)     // Catch:{ Exception -> 0x01d4 }
            r9 = r0
            java.io.BufferedInputStream r0 = new java.io.BufferedInputStream     // Catch:{ all -> 0x01c6 }
            r0.<init>(r9)     // Catch:{ all -> 0x01c6 }
            r10 = r0
            java.io.DataInputStream r0 = new java.io.DataInputStream     // Catch:{ all -> 0x01b7 }
            r0.<init>(r10)     // Catch:{ all -> 0x01b7 }
            r11 = r0
            int r0 = r11.readInt()     // Catch:{ all -> 0x01a8 }
            r12 = r0
            r0 = 1
            r13 = 0
            if (r12 == r0) goto L_0x0065
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0060 }
            r0.<init>()     // Catch:{ all -> 0x0060 }
            java.lang.String r2 = "Unknown backup schedule version "
            r0.append(r2)     // Catch:{ all -> 0x0060 }
            r0.append(r12)     // Catch:{ all -> 0x0060 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0060 }
            android.util.Slog.e(r3, r0)     // Catch:{ all -> 0x0060 }
            $closeResource(r13, r11)     // Catch:{ all -> 0x005b }
            $closeResource(r13, r10)     // Catch:{ all -> 0x0056 }
            $closeResource(r13, r9)     // Catch:{ Exception -> 0x01d2 }
            return r13
        L_0x0056:
            r0 = move-exception
            r2 = r0
            r7 = r9
            goto L_0x01cb
        L_0x005b:
            r0 = move-exception
            r2 = r0
            r7 = r9
            goto L_0x01bc
        L_0x0060:
            r0 = move-exception
            r2 = r0
            r7 = r9
            goto L_0x01ad
        L_0x0065:
            int r0 = r11.readInt()     // Catch:{ all -> 0x01a8 }
            r14 = r0
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x01a8 }
            r0.<init>(r14)     // Catch:{ all -> 0x01a8 }
            r5 = r0
            java.util.HashSet r0 = new java.util.HashSet     // Catch:{ all -> 0x01a8 }
            r0.<init>(r14)     // Catch:{ all -> 0x01a8 }
            r15 = r0
            r0 = 0
            r13 = r0
        L_0x0078:
            if (r13 >= r14) goto L_0x011c
            java.lang.String r0 = r11.readUTF()     // Catch:{ all -> 0x0115 }
            r16 = r0
            long r17 = r11.readLong()     // Catch:{ all -> 0x0115 }
            r19 = r17
            r7 = r16
            r15.add(r7)     // Catch:{ all -> 0x0115 }
            android.content.pm.PackageManager r0 = r1.mPackageManager     // Catch:{ NameNotFoundException -> 0x00e5 }
            int r8 = r1.mUserId     // Catch:{ NameNotFoundException -> 0x00e5 }
            r21 = r4
            r4 = 0
            android.content.pm.PackageInfo r0 = r0.getPackageInfoAsUser(r7, r4, r8)     // Catch:{ NameNotFoundException -> 0x00df, all -> 0x00d8 }
            boolean r4 = com.android.server.backup.utils.AppBackupUtils.appGetsFullBackup(r0)     // Catch:{ NameNotFoundException -> 0x00df, all -> 0x00d8 }
            if (r4 == 0) goto L_0x00b8
            android.content.pm.ApplicationInfo r4 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x00df, all -> 0x00d8 }
            int r8 = r1.mUserId     // Catch:{ NameNotFoundException -> 0x00df, all -> 0x00d8 }
            boolean r4 = com.android.server.backup.utils.AppBackupUtils.appIsEligibleForBackup(r4, r8)     // Catch:{ NameNotFoundException -> 0x00df, all -> 0x00d8 }
            if (r4 == 0) goto L_0x00b3
            com.android.server.backup.fullbackup.FullBackupEntry r4 = new com.android.server.backup.fullbackup.FullBackupEntry     // Catch:{ NameNotFoundException -> 0x00df, all -> 0x00d8 }
            r16 = r9
            r8 = r19
            r4.<init>(r7, r8)     // Catch:{ NameNotFoundException -> 0x00d6 }
            r5.add(r4)     // Catch:{ NameNotFoundException -> 0x00d6 }
            goto L_0x00d5
        L_0x00b3:
            r16 = r9
            r8 = r19
            goto L_0x00bc
        L_0x00b8:
            r16 = r9
            r8 = r19
        L_0x00bc:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x00d6 }
            r4.<init>()     // Catch:{ NameNotFoundException -> 0x00d6 }
            r4.append(r2)     // Catch:{ NameNotFoundException -> 0x00d6 }
            r4.append(r7)     // Catch:{ NameNotFoundException -> 0x00d6 }
            r19 = r0
            java.lang.String r0 = " no longer eligible for full backup"
            r4.append(r0)     // Catch:{ NameNotFoundException -> 0x00d6 }
            java.lang.String r0 = r4.toString()     // Catch:{ NameNotFoundException -> 0x00d6 }
            android.util.Slog.i(r3, r0)     // Catch:{ NameNotFoundException -> 0x00d6 }
        L_0x00d5:
            goto L_0x0105
        L_0x00d6:
            r0 = move-exception
            goto L_0x00ec
        L_0x00d8:
            r0 = move-exception
            r2 = r0
            r7 = r9
            r4 = r21
            goto L_0x01ad
        L_0x00df:
            r0 = move-exception
            r16 = r9
            r8 = r19
            goto L_0x00ec
        L_0x00e5:
            r0 = move-exception
            r21 = r4
            r16 = r9
            r8 = r19
        L_0x00ec:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x010d }
            r4.<init>()     // Catch:{ all -> 0x010d }
            r4.append(r2)     // Catch:{ all -> 0x010d }
            r4.append(r7)     // Catch:{ all -> 0x010d }
            r19 = r0
            java.lang.String r0 = " not installed; dropping from full backup"
            r4.append(r0)     // Catch:{ all -> 0x010d }
            java.lang.String r0 = r4.toString()     // Catch:{ all -> 0x010d }
            android.util.Slog.i(r3, r0)     // Catch:{ all -> 0x010d }
        L_0x0105:
            int r13 = r13 + 1
            r9 = r16
            r4 = r21
            goto L_0x0078
        L_0x010d:
            r0 = move-exception
            r2 = r0
            r7 = r16
            r4 = r21
            goto L_0x01ad
        L_0x0115:
            r0 = move-exception
            r21 = r4
            r2 = r0
            r7 = r9
            goto L_0x01ad
        L_0x011c:
            r21 = r4
            r16 = r9
            java.util.Iterator r0 = r6.iterator()     // Catch:{ all -> 0x01a1 }
            r4 = r21
        L_0x0126:
            boolean r2 = r0.hasNext()     // Catch:{ all -> 0x019c }
            if (r2 == 0) goto L_0x0180
            java.lang.Object r2 = r0.next()     // Catch:{ all -> 0x017b }
            android.content.pm.PackageInfo r2 = (android.content.pm.PackageInfo) r2     // Catch:{ all -> 0x017b }
            boolean r7 = com.android.server.backup.utils.AppBackupUtils.appGetsFullBackup(r2)     // Catch:{ all -> 0x017b }
            if (r7 == 0) goto L_0x0178
            android.content.pm.ApplicationInfo r7 = r2.applicationInfo     // Catch:{ all -> 0x017b }
            int r8 = r1.mUserId     // Catch:{ all -> 0x017b }
            boolean r7 = com.android.server.backup.utils.AppBackupUtils.appIsEligibleForBackup(r7, r8)     // Catch:{ all -> 0x017b }
            if (r7 == 0) goto L_0x0176
            java.lang.String r7 = r2.packageName     // Catch:{ all -> 0x017b }
            boolean r7 = r15.contains(r7)     // Catch:{ all -> 0x017b }
            if (r7 != 0) goto L_0x0174
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x017b }
            r7.<init>()     // Catch:{ all -> 0x017b }
            java.lang.String r8 = "New full backup app "
            r7.append(r8)     // Catch:{ all -> 0x017b }
            java.lang.String r8 = r2.packageName     // Catch:{ all -> 0x017b }
            r7.append(r8)     // Catch:{ all -> 0x017b }
            java.lang.String r8 = " found"
            r7.append(r8)     // Catch:{ all -> 0x017b }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x017b }
            android.util.Slog.i(r3, r7)     // Catch:{ all -> 0x017b }
            com.android.server.backup.fullbackup.FullBackupEntry r7 = new com.android.server.backup.fullbackup.FullBackupEntry     // Catch:{ all -> 0x017b }
            java.lang.String r8 = r2.packageName     // Catch:{ all -> 0x017b }
            r9 = r12
            r12 = 0
            r7.<init>(r8, r12)     // Catch:{ all -> 0x017b }
            r5.add(r7)     // Catch:{ all -> 0x017b }
            r4 = 1
            goto L_0x0179
        L_0x0174:
            r9 = r12
            goto L_0x0179
        L_0x0176:
            r9 = r12
            goto L_0x0179
        L_0x0178:
            r9 = r12
        L_0x0179:
            r12 = r9
            goto L_0x0126
        L_0x017b:
            r0 = move-exception
            r2 = r0
            r7 = r16
            goto L_0x01ad
        L_0x0180:
            r9 = r12
            java.util.Collections.sort(r5)     // Catch:{ all -> 0x019c }
            r2 = 0
            $closeResource(r2, r11)     // Catch:{ all -> 0x0197 }
            $closeResource(r2, r10)     // Catch:{ all -> 0x0192 }
            r7 = r16
            $closeResource(r2, r7)     // Catch:{ Exception -> 0x01d2 }
            goto L_0x01e5
        L_0x0192:
            r0 = move-exception
            r7 = r16
            r2 = r0
            goto L_0x01cb
        L_0x0197:
            r0 = move-exception
            r7 = r16
            r2 = r0
            goto L_0x01bc
        L_0x019c:
            r0 = move-exception
            r7 = r16
            r2 = r0
            goto L_0x01ad
        L_0x01a1:
            r0 = move-exception
            r7 = r16
            r2 = r0
            r4 = r21
            goto L_0x01ad
        L_0x01a8:
            r0 = move-exception
            r21 = r4
            r7 = r9
            r2 = r0
        L_0x01ad:
            throw r2     // Catch:{ all -> 0x01ae }
        L_0x01ae:
            r0 = move-exception
            r8 = r0
            $closeResource(r2, r11)     // Catch:{ all -> 0x01b4 }
            throw r8     // Catch:{ all -> 0x01b4 }
        L_0x01b4:
            r0 = move-exception
            r2 = r0
            goto L_0x01bc
        L_0x01b7:
            r0 = move-exception
            r21 = r4
            r7 = r9
            r2 = r0
        L_0x01bc:
            throw r2     // Catch:{ all -> 0x01bd }
        L_0x01bd:
            r0 = move-exception
            r8 = r0
            $closeResource(r2, r10)     // Catch:{ all -> 0x01c3 }
            throw r8     // Catch:{ all -> 0x01c3 }
        L_0x01c3:
            r0 = move-exception
            r2 = r0
            goto L_0x01cb
        L_0x01c6:
            r0 = move-exception
            r21 = r4
            r7 = r9
            r2 = r0
        L_0x01cb:
            throw r2     // Catch:{ all -> 0x01cc }
        L_0x01cc:
            r0 = move-exception
            r8 = r0
            $closeResource(r2, r7)     // Catch:{ Exception -> 0x01d2 }
            throw r8     // Catch:{ Exception -> 0x01d2 }
        L_0x01d2:
            r0 = move-exception
            goto L_0x01d7
        L_0x01d4:
            r0 = move-exception
            r21 = r4
        L_0x01d7:
            java.lang.String r2 = "Unable to read backup schedule"
            android.util.Slog.e(r3, r2, r0)
            java.io.File r2 = r1.mFullBackupScheduleFile
            r2.delete()
            r5 = 0
            goto L_0x01e5
        L_0x01e3:
            r21 = r4
        L_0x01e5:
            if (r5 != 0) goto L_0x0222
            r4 = 1
            java.util.ArrayList r0 = new java.util.ArrayList
            int r2 = r6.size()
            r0.<init>(r2)
            r5 = r0
            java.util.Iterator r0 = r6.iterator()
        L_0x01f6:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x0222
            java.lang.Object r2 = r0.next()
            android.content.pm.PackageInfo r2 = (android.content.pm.PackageInfo) r2
            boolean r3 = com.android.server.backup.utils.AppBackupUtils.appGetsFullBackup(r2)
            if (r3 == 0) goto L_0x021f
            android.content.pm.ApplicationInfo r3 = r2.applicationInfo
            int r7 = r1.mUserId
            boolean r3 = com.android.server.backup.utils.AppBackupUtils.appIsEligibleForBackup(r3, r7)
            if (r3 == 0) goto L_0x021f
            com.android.server.backup.fullbackup.FullBackupEntry r3 = new com.android.server.backup.fullbackup.FullBackupEntry
            java.lang.String r7 = r2.packageName
            r8 = 0
            r3.<init>(r7, r8)
            r5.add(r3)
            goto L_0x0221
        L_0x021f:
            r8 = 0
        L_0x0221:
            goto L_0x01f6
        L_0x0222:
            if (r4 == 0) goto L_0x0227
            r22.writeFullBackupScheduleAsync()
        L_0x0227:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.readFullBackupSchedule():java.util.ArrayList");
    }

    /* access modifiers changed from: private */
    public void writeFullBackupScheduleAsync() {
        this.mBackupHandler.removeCallbacks(this.mFullBackupScheduleWriter);
        this.mBackupHandler.post(this.mFullBackupScheduleWriter);
    }

    /* access modifiers changed from: private */
    public void parseLeftoverJournals() {
        Iterator<DataChangedJournal> it = DataChangedJournal.listJournals(this.mJournalDir).iterator();
        while (it.hasNext()) {
            DataChangedJournal journal = it.next();
            if (!journal.equals(this.mJournal)) {
                try {
                    journal.forEach(new Consumer() {
                        public final void accept(Object obj) {
                            UserBackupManagerService.this.lambda$parseLeftoverJournals$0$UserBackupManagerService((String) obj);
                        }
                    });
                } catch (IOException e) {
                    Slog.e(BackupManagerService.TAG, "Can't read " + journal, e);
                }
            }
        }
    }

    public /* synthetic */ void lambda$parseLeftoverJournals$0$UserBackupManagerService(String packageName) {
        Slog.i(BackupManagerService.TAG, "Found stale backup journal, scheduling");
        Slog.i(BackupManagerService.TAG, "  " + packageName);
        dataChangedImpl(packageName);
    }

    public byte[] randomBytes(int bits) {
        byte[] array = new byte[(bits / 8)];
        this.mRng.nextBytes(array);
        return array;
    }

    public boolean setBackupPassword(String currentPw, String newPw) {
        return this.mBackupPasswordManager.setBackupPassword(currentPw, newPw);
    }

    public boolean hasBackupPassword() {
        return this.mBackupPasswordManager.hasBackupPassword();
    }

    public boolean backupPasswordMatches(String currentPw) {
        return this.mBackupPasswordManager.backupPasswordMatches(currentPw);
    }

    public void recordInitPending(boolean isPending, String transportName, String transportDirName) {
        synchronized (this.mQueueLock) {
            Slog.i(BackupManagerService.TAG, "recordInitPending(" + isPending + ") on transport " + transportName);
            File initPendingFile = new File(new File(this.mBaseStateDir, transportDirName), INIT_SENTINEL_FILE_NAME);
            if (isPending) {
                this.mPendingInits.add(transportName);
                try {
                    new FileOutputStream(initPendingFile).close();
                } catch (IOException e) {
                }
            } else {
                initPendingFile.delete();
                this.mPendingInits.remove(transportName);
            }
        }
    }

    public void resetBackupState(File stateFileDir) {
        synchronized (this.mQueueLock) {
            this.mProcessedPackagesJournal.reset();
            this.mCurrentToken = 0;
            writeRestoreTokens();
            for (File sf : stateFileDir.listFiles()) {
                if (!sf.getName().equals(INIT_SENTINEL_FILE_NAME)) {
                    sf.delete();
                }
            }
        }
        synchronized (this.mBackupParticipants) {
            int numParticipants = this.mBackupParticipants.size();
            for (int i = 0; i < numParticipants; i++) {
                HashSet<String> participants = this.mBackupParticipants.valueAt(i);
                if (participants != null) {
                    Iterator<String> it = participants.iterator();
                    while (it.hasNext()) {
                        dataChangedImpl(it.next());
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void onTransportRegistered(String transportName, String transportDirName) {
        long timeMs = SystemClock.elapsedRealtime() - this.mRegisterTransportsRequestedTime;
        Slog.d(BackupManagerService.TAG, "Transport " + transportName + " registered " + timeMs + "ms after first request (delay = " + 3000 + "ms)");
        File stateDir = new File(this.mBaseStateDir, transportDirName);
        stateDir.mkdirs();
        if (new File(stateDir, INIT_SENTINEL_FILE_NAME).exists()) {
            synchronized (this.mQueueLock) {
                this.mPendingInits.add(transportName);
                this.mAlarmManager.set(0, System.currentTimeMillis() + 60000, this.mRunInitIntent);
            }
        }
    }

    /* access modifiers changed from: private */
    public void addPackageParticipantsLocked(String[] packageNames) {
        List<PackageInfo> targetApps = allAgentPackages();
        if (packageNames != null) {
            Slog.v(BackupManagerService.TAG, "addPackageParticipantsLocked: #" + packageNames.length);
            for (String packageName : packageNames) {
                addPackageParticipantsLockedInner(packageName, targetApps);
            }
            return;
        }
        Slog.v(BackupManagerService.TAG, "addPackageParticipantsLocked: all");
        addPackageParticipantsLockedInner((String) null, targetApps);
    }

    private void addPackageParticipantsLockedInner(String packageName, List<PackageInfo> targetPkgs) {
        Slog.v(BackupManagerService.TAG, "Examining " + packageName + " for backup agent");
        for (PackageInfo pkg : targetPkgs) {
            if (packageName == null || pkg.packageName.equals(packageName)) {
                int uid = pkg.applicationInfo.uid;
                HashSet<String> set = this.mBackupParticipants.get(uid);
                if (set == null) {
                    set = new HashSet<>();
                    this.mBackupParticipants.put(uid, set);
                }
                set.add(pkg.packageName);
                Slog.v(BackupManagerService.TAG, "Agent found; added");
                Slog.i(BackupManagerService.TAG, "Scheduling backup for new app " + pkg.packageName);
                this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(16, pkg.packageName));
            }
        }
    }

    /* access modifiers changed from: private */
    public void removePackageParticipantsLocked(String[] packageNames, int oldUid) {
        if (packageNames == null) {
            Slog.w(BackupManagerService.TAG, "removePackageParticipants with null list");
            return;
        }
        Slog.v(BackupManagerService.TAG, "removePackageParticipantsLocked: uid=" + oldUid + " #" + packageNames.length);
        for (String pkg : packageNames) {
            HashSet<String> set = this.mBackupParticipants.get(oldUid);
            if (set != null && set.contains(pkg)) {
                removePackageFromSetLocked(set, pkg);
                if (set.isEmpty()) {
                    Slog.v(BackupManagerService.TAG, "  last one of this uid; purging set");
                    this.mBackupParticipants.remove(oldUid);
                }
            }
        }
    }

    private void removePackageFromSetLocked(HashSet<String> set, String packageName) {
        if (set.contains(packageName)) {
            Slog.v(BackupManagerService.TAG, "  removing participant " + packageName);
            set.remove(packageName);
            this.mPendingBackups.remove(packageName);
        }
    }

    private List<PackageInfo> allAgentPackages() {
        List<PackageInfo> packages = this.mPackageManager.getInstalledPackagesAsUser(134217728, this.mUserId);
        for (int a = packages.size() - 1; a >= 0; a--) {
            PackageInfo pkg = packages.get(a);
            try {
                ApplicationInfo app = pkg.applicationInfo;
                if (!((app.flags & 32768) == 0 || app.backupAgentName == null)) {
                    if ((app.flags & BroadcastQueueInjector.FLAG_IMMUTABLE) == 0) {
                        ApplicationInfo app2 = this.mPackageManager.getApplicationInfoAsUser(pkg.packageName, 1024, this.mUserId);
                        pkg.applicationInfo.sharedLibraryFiles = app2.sharedLibraryFiles;
                        pkg.applicationInfo.sharedLibraryInfos = app2.sharedLibraryInfos;
                    }
                }
                packages.remove(a);
            } catch (PackageManager.NameNotFoundException e) {
                packages.remove(a);
            }
        }
        return packages;
    }

    public void logBackupComplete(String packageName) {
        if (!packageName.equals(PACKAGE_MANAGER_SENTINEL)) {
            for (String receiver : this.mConstants.getBackupFinishedNotificationReceivers()) {
                Intent notification = new Intent();
                notification.setAction(BACKUP_FINISHED_ACTION);
                notification.setPackage(receiver);
                notification.addFlags(268435488);
                notification.putExtra(BACKUP_FINISHED_PACKAGE_EXTRA, packageName);
                this.mContext.sendBroadcastAsUser(notification, UserHandle.of(this.mUserId));
            }
            this.mProcessedPackagesJournal.addPackage(packageName);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0078, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        $closeResource(r2, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x007c, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeRestoreTokens() {
        /*
            r7 = this;
            java.lang.String r0 = "BackupManagerService"
            java.io.RandomAccessFile r1 = new java.io.RandomAccessFile     // Catch:{ IOException -> 0x007d }
            java.io.File r2 = r7.mTokenFile     // Catch:{ IOException -> 0x007d }
            java.lang.String r3 = "rwd"
            r1.<init>(r2, r3)     // Catch:{ IOException -> 0x007d }
            r2 = 0
            r3 = 1
            r1.writeInt(r3)     // Catch:{ all -> 0x0076 }
            long r3 = r7.mAncestralToken     // Catch:{ all -> 0x0076 }
            r1.writeLong(r3)     // Catch:{ all -> 0x0076 }
            long r3 = r7.mCurrentToken     // Catch:{ all -> 0x0076 }
            r1.writeLong(r3)     // Catch:{ all -> 0x0076 }
            java.util.Set<java.lang.String> r3 = r7.mAncestralPackages     // Catch:{ all -> 0x0076 }
            if (r3 != 0) goto L_0x0024
            r3 = -1
            r1.writeInt(r3)     // Catch:{ all -> 0x0076 }
            goto L_0x0072
        L_0x0024:
            java.util.Set<java.lang.String> r3 = r7.mAncestralPackages     // Catch:{ all -> 0x0076 }
            int r3 = r3.size()     // Catch:{ all -> 0x0076 }
            r1.writeInt(r3)     // Catch:{ all -> 0x0076 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0076 }
            r3.<init>()     // Catch:{ all -> 0x0076 }
            java.lang.String r4 = "Ancestral packages:  "
            r3.append(r4)     // Catch:{ all -> 0x0076 }
            java.util.Set<java.lang.String> r4 = r7.mAncestralPackages     // Catch:{ all -> 0x0076 }
            int r4 = r4.size()     // Catch:{ all -> 0x0076 }
            r3.append(r4)     // Catch:{ all -> 0x0076 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0076 }
            android.util.Slog.v(r0, r3)     // Catch:{ all -> 0x0076 }
            java.util.Set<java.lang.String> r3 = r7.mAncestralPackages     // Catch:{ all -> 0x0076 }
            java.util.Iterator r3 = r3.iterator()     // Catch:{ all -> 0x0076 }
        L_0x004d:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x0076 }
            if (r4 == 0) goto L_0x0072
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x0076 }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ all -> 0x0076 }
            r1.writeUTF(r4)     // Catch:{ all -> 0x0076 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0076 }
            r5.<init>()     // Catch:{ all -> 0x0076 }
            java.lang.String r6 = "   "
            r5.append(r6)     // Catch:{ all -> 0x0076 }
            r5.append(r4)     // Catch:{ all -> 0x0076 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0076 }
            android.util.Slog.v(r0, r5)     // Catch:{ all -> 0x0076 }
            goto L_0x004d
        L_0x0072:
            $closeResource(r2, r1)     // Catch:{ IOException -> 0x007d }
            goto L_0x0083
        L_0x0076:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x0078 }
        L_0x0078:
            r3 = move-exception
            $closeResource(r2, r1)     // Catch:{ IOException -> 0x007d }
            throw r3     // Catch:{ IOException -> 0x007d }
        L_0x007d:
            r1 = move-exception
            java.lang.String r2 = "Unable to write token file:"
            android.util.Slog.w(r0, r2, r1)
        L_0x0083:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.writeRestoreTokens():void");
    }

    public IBackupAgent bindToAgentSynchronous(ApplicationInfo app, int mode) {
        IBackupAgent agent = null;
        synchronized (this.mAgentConnectLock) {
            this.mConnecting = true;
            this.mConnectedAgent = null;
            try {
                int userIdX = BackupManagerServiceInjector.getAppUserId(this.mCallerFd, this.mUserId);
                Slog.d(BackupManagerService.TAG, "bindToAgentSynchronous, packageName:" + app.packageName + " uid:" + app.uid + ", userId:" + userIdX);
                if (this.mActivityManager.bindBackupAgent(app.packageName, mode, userIdX)) {
                    Slog.d(BackupManagerService.TAG, "awaiting agent for " + app);
                    long timeoutMark = System.currentTimeMillis() + 10000;
                    while (this.mConnecting && this.mConnectedAgent == null && System.currentTimeMillis() < timeoutMark) {
                        try {
                            this.mAgentConnectLock.wait(5000);
                        } catch (InterruptedException e) {
                            Slog.w(BackupManagerService.TAG, "Interrupted: " + e);
                            this.mConnecting = false;
                            this.mConnectedAgent = null;
                        }
                    }
                    if (this.mConnecting) {
                        Slog.w(BackupManagerService.TAG, "Timeout waiting for agent " + app);
                        this.mConnectedAgent = null;
                    }
                    Slog.i(BackupManagerService.TAG, "got agent " + this.mConnectedAgent);
                    agent = this.mConnectedAgent;
                }
            } catch (RemoteException e2) {
            }
        }
        if (agent == null) {
            int userIdX2 = BackupManagerServiceInjector.getAppUserId(this.mCallerFd, this.mUserId);
            Slog.d(BackupManagerService.TAG, "clearPendingBackup, packageName:" + app.packageName + " uid:" + app.uid + ", userId:" + userIdX2);
            this.mActivityManagerInternal.clearPendingBackup(userIdX2);
        }
        return agent;
    }

    public void unbindAgent(ApplicationInfo app) {
        try {
            this.mActivityManager.unbindBackupAgent(app);
        } catch (RemoteException e) {
        }
    }

    public void clearApplicationDataAfterRestoreFailure(String packageName) {
        clearApplicationDataSynchronous(packageName, true, false);
    }

    public void clearApplicationDataBeforeRestore(String packageName) {
        clearApplicationDataSynchronous(packageName, false, true);
    }

    private void clearApplicationDataSynchronous(String packageName, boolean checkFlagAllowClearUserDataOnFailedRestore, boolean keepSystemState) {
        boolean shouldClearData;
        int userId = BackupManagerServiceInjector.getAppUserId(this.mCallerFd, this.mUserId);
        try {
            ApplicationInfo applicationInfo = this.mPackageManager.getPackageInfoAsUser(packageName, 0, userId).applicationInfo;
            if (!checkFlagAllowClearUserDataOnFailedRestore || applicationInfo.targetSdkVersion < 29) {
                shouldClearData = (applicationInfo.flags & 64) != 0;
            } else {
                shouldClearData = (applicationInfo.privateFlags & BroadcastQueueInjector.FLAG_IMMUTABLE) != 0;
            }
            if (!shouldClearData) {
                Slog.i(BackupManagerService.TAG, "Clearing app data is not allowed so not wiping " + packageName);
                return;
            }
            ClearDataObserver observer = new ClearDataObserver(this);
            synchronized (this.mClearDataLock) {
                this.mClearingData = true;
                try {
                    this.mActivityManager.clearApplicationUserData(packageName, keepSystemState, observer, userId);
                } catch (RemoteException e) {
                }
                long timeoutMark = System.currentTimeMillis() + 30000;
                while (this.mClearingData && System.currentTimeMillis() < timeoutMark) {
                    try {
                        this.mClearDataLock.wait(5000);
                    } catch (InterruptedException e2) {
                        this.mClearingData = false;
                        Slog.w(BackupManagerService.TAG, "Interrupted while waiting for " + packageName + " data to be cleared", e2);
                    }
                }
                if (this.mClearingData) {
                    Slog.w(BackupManagerService.TAG, "Clearing app data for " + packageName + " timed out");
                }
            }
        } catch (PackageManager.NameNotFoundException e3) {
            Slog.w(BackupManagerService.TAG, "Tried to clear data for " + packageName + " but not found");
        }
    }

    public long getAvailableRestoreToken(String packageName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getAvailableRestoreToken");
        long token = this.mAncestralToken;
        synchronized (this.mQueueLock) {
            if (this.mCurrentToken != 0 && this.mProcessedPackagesJournal.hasBeenProcessed(packageName)) {
                Slog.i(BackupManagerService.TAG, "App in ever-stored, so using current token");
                token = this.mCurrentToken;
            }
        }
        Slog.i(BackupManagerService.TAG, "getAvailableRestoreToken() == " + token);
        return token;
    }

    public int requestBackup(String[] packages, IBackupObserver observer, int flags) {
        return requestBackup(packages, observer, (IBackupManagerMonitor) null, flags);
    }

    public int requestBackup(String[] packages, IBackupObserver observer, IBackupManagerMonitor monitor, int flags) {
        int logTag;
        String[] strArr = packages;
        IBackupObserver iBackupObserver = observer;
        IBackupManagerMonitor iBackupManagerMonitor = monitor;
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "requestBackup");
        if (strArr == null || strArr.length < 1) {
            Slog.e(BackupManagerService.TAG, "No packages named for backup request");
            BackupObserverUtils.sendBackupFinished(iBackupObserver, JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE);
            IBackupManagerMonitor monitorEvent = BackupManagerMonitorUtils.monitorEvent(iBackupManagerMonitor, 49, (PackageInfo) null, 1, (Bundle) null);
            throw new IllegalArgumentException("No packages are provided for backup");
        } else if (!this.mEnabled || !this.mSetupComplete) {
            Slog.i(BackupManagerService.TAG, "Backup requested but enabled=" + this.mEnabled + " setupComplete=" + this.mSetupComplete);
            BackupObserverUtils.sendBackupFinished(iBackupObserver, -2001);
            if (this.mSetupComplete) {
                logTag = 13;
            } else {
                logTag = 14;
            }
            IBackupManagerMonitor monitorEvent2 = BackupManagerMonitorUtils.monitorEvent(iBackupManagerMonitor, logTag, (PackageInfo) null, 3, (Bundle) null);
            return -2001;
        } else {
            try {
                String transportDirName = this.mTransportManager.getTransportDirName(this.mTransportManager.getCurrentTransportName());
                TransportClient transportClient = this.mTransportManager.getCurrentTransportClientOrThrow("BMS.requestBackup()");
                $$Lambda$UserBackupManagerService$sAYsrY5C5zAl7EgKgwo188kx6JE r11 = new OnTaskFinishedListener(transportClient) {
                    private final /* synthetic */ TransportClient f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onFinished(String str) {
                        UserBackupManagerService.this.lambda$requestBackup$1$UserBackupManagerService(this.f$1, str);
                    }
                };
                ArrayList<String> fullBackupList = new ArrayList<>();
                ArrayList<String> kvBackupList = new ArrayList<>();
                for (String packageName : strArr) {
                    if (PACKAGE_MANAGER_SENTINEL.equals(packageName)) {
                        kvBackupList.add(packageName);
                    } else {
                        try {
                            PackageInfo packageInfo = this.mPackageManager.getPackageInfoAsUser(packageName, 134217728, this.mUserId);
                            if (!AppBackupUtils.appIsEligibleForBackup(packageInfo.applicationInfo, this.mUserId)) {
                                BackupObserverUtils.sendBackupOnPackageResult(iBackupObserver, packageName, -2001);
                            } else if (AppBackupUtils.appGetsFullBackup(packageInfo)) {
                                fullBackupList.add(packageInfo.packageName);
                            } else {
                                kvBackupList.add(packageInfo.packageName);
                            }
                        } catch (PackageManager.NameNotFoundException e) {
                            BackupObserverUtils.sendBackupOnPackageResult(iBackupObserver, packageName, -2002);
                        }
                    }
                }
                EventLog.writeEvent(EventLogTags.BACKUP_REQUESTED, new Object[]{Integer.valueOf(strArr.length), Integer.valueOf(kvBackupList.size()), Integer.valueOf(fullBackupList.size())});
                Slog.i(BackupManagerService.TAG, "Backup requested for " + strArr.length + " packages, of them: " + fullBackupList.size() + " full backups, " + kvBackupList.size() + " k/v backups");
                boolean nonIncrementalBackup = (flags & 1) != 0;
                Message msg = this.mBackupHandler.obtainMessage(15);
                ArrayList<String> arrayList = kvBackupList;
                BackupParams backupParams = r3;
                ArrayList<String> arrayList2 = fullBackupList;
                $$Lambda$UserBackupManagerService$sAYsrY5C5zAl7EgKgwo188kx6JE r20 = r11;
                BackupParams backupParams2 = new BackupParams(transportClient, transportDirName, kvBackupList, fullBackupList, observer, monitor, r11, true, nonIncrementalBackup);
                msg.obj = backupParams;
                this.mBackupHandler.sendMessage(msg);
                return 0;
            } catch (TransportNotRegisteredException e2) {
                BackupObserverUtils.sendBackupFinished(iBackupObserver, JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE);
                IBackupManagerMonitor monitorEvent3 = BackupManagerMonitorUtils.monitorEvent(iBackupManagerMonitor, 50, (PackageInfo) null, 1, (Bundle) null);
                return JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
            }
        }
    }

    public /* synthetic */ void lambda$requestBackup$1$UserBackupManagerService(TransportClient transportClient, String caller) {
        this.mTransportManager.disposeOfTransportClient(transportClient, caller);
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public void cancelBackups() {
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "cancelBackups");
        Slog.i(BackupManagerService.TAG, "cancelBackups() called.");
        long oldToken = Binder.clearCallingIdentity();
        try {
            List<Integer> operationsToCancel = new ArrayList<>();
            synchronized (this.mCurrentOpLock) {
                for (int i = 0; i < this.mCurrentOperations.size(); i++) {
                    int token = this.mCurrentOperations.keyAt(i);
                    if (this.mCurrentOperations.valueAt(i).type == 2) {
                        operationsToCancel.add(Integer.valueOf(token));
                    }
                }
            }
            for (Integer token2 : operationsToCancel) {
                handleCancel(token2.intValue(), true);
            }
            KeyValueBackupJob.schedule(this.mUserId, this.mContext, 3600000, this.mConstants);
            FullBackupJob.schedule(this.mUserId, this.mContext, 7200000, this.mConstants);
            Binder.restoreCallingIdentity(oldToken);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(oldToken);
            throw th;
        }
    }

    public void prepareOperationTimeout(int token, long interval, BackupRestoreTask callback, int operationType) {
        if (BackupManagerServiceInjector.isCanceling(this.mCallerFd)) {
            Slog.i(BackupManagerService.TAG, "prepareOperationTimeout() doesn't support operation, isCanceling !");
        } else if (operationType == 0 || operationType == 1) {
            Slog.v(BackupManagerService.TAG, "starting timeout: token=" + Integer.toHexString(token) + " interval=" + interval + " callback=" + callback);
            synchronized (this.mCurrentOpLock) {
                this.mCurrentOperations.put(token, new Operation(0, callback, operationType));
                this.mBackupHandler.sendMessageDelayed(this.mBackupHandler.obtainMessage(getMessageIdForOperationType(operationType), token, 0, callback), interval);
            }
        } else {
            Slog.wtf(BackupManagerService.TAG, "prepareOperationTimeout() doesn't support operation " + Integer.toHexString(token) + " of type " + operationType);
        }
    }

    private int getMessageIdForOperationType(int operationType) {
        if (operationType == 0) {
            return 17;
        }
        if (operationType == 1) {
            return 18;
        }
        Slog.wtf(BackupManagerService.TAG, "getMessageIdForOperationType called on invalid operation type: " + operationType);
        return -1;
    }

    public void putOperation(int token, Operation operation) {
        Slog.d(BackupManagerService.TAG, "Adding operation token=" + Integer.toHexString(token) + ", operation type=" + operation.type);
        synchronized (this.mCurrentOpLock) {
            this.mCurrentOperations.put(token, operation);
        }
    }

    public void removeOperation(int token) {
        Slog.d(BackupManagerService.TAG, "Removing operation token=" + Integer.toHexString(token));
        synchronized (this.mCurrentOpLock) {
            if (this.mCurrentOperations.get(token) == null) {
                Slog.w(BackupManagerService.TAG, "Duplicate remove for operation. token=" + Integer.toHexString(token));
            }
            this.mCurrentOperations.remove(token);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        android.util.Slog.d(com.android.server.backup.BackupManagerService.TAG, "Unblocked waiting for operation token=" + java.lang.Integer.toHexString(r7));
        r0 = r1.state;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean waitUntilOperationComplete(int r7) {
        /*
            r6 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Blocking until operation complete for "
            r0.append(r1)
            java.lang.String r1 = java.lang.Integer.toHexString(r7)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "BackupManagerService"
            android.util.Slog.i(r1, r0)
            r0 = 0
            r1 = 0
            java.lang.Object r2 = r6.mCurrentOpLock
            monitor-enter(r2)
        L_0x001f:
            android.util.SparseArray<com.android.server.backup.internal.Operation> r3 = r6.mCurrentOperations     // Catch:{ all -> 0x008f }
            java.lang.Object r3 = r3.get(r7)     // Catch:{ all -> 0x008f }
            com.android.server.backup.internal.Operation r3 = (com.android.server.backup.internal.Operation) r3     // Catch:{ all -> 0x008f }
            r1 = r3
            if (r1 != 0) goto L_0x002b
            goto L_0x0055
        L_0x002b:
            int r3 = r1.state     // Catch:{ all -> 0x008f }
            if (r3 != 0) goto L_0x0037
            java.lang.Object r3 = r6.mCurrentOpLock     // Catch:{ InterruptedException -> 0x0035 }
            r3.wait()     // Catch:{ InterruptedException -> 0x0035 }
            goto L_0x0036
        L_0x0035:
            r3 = move-exception
        L_0x0036:
            goto L_0x001f
        L_0x0037:
            java.lang.String r3 = "BackupManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x008f }
            r4.<init>()     // Catch:{ all -> 0x008f }
            java.lang.String r5 = "Unblocked waiting for operation token="
            r4.append(r5)     // Catch:{ all -> 0x008f }
            java.lang.String r5 = java.lang.Integer.toHexString(r7)     // Catch:{ all -> 0x008f }
            r4.append(r5)     // Catch:{ all -> 0x008f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x008f }
            android.util.Slog.d(r3, r4)     // Catch:{ all -> 0x008f }
            int r3 = r1.state     // Catch:{ all -> 0x008f }
            r0 = r3
        L_0x0055:
            monitor-exit(r2)     // Catch:{ all -> 0x008f }
            r6.removeOperation(r7)
            if (r1 == 0) goto L_0x0066
            com.android.server.backup.internal.BackupHandler r2 = r6.mBackupHandler
            int r3 = r1.type
            int r3 = r6.getMessageIdForOperationType(r3)
            r2.removeMessages(r3)
        L_0x0066:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "operation "
            r2.append(r3)
            java.lang.String r3 = java.lang.Integer.toHexString(r7)
            r2.append(r3)
            java.lang.String r3 = " complete: finalState="
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BackupManagerService"
            android.util.Slog.v(r3, r2)
            r2 = 1
            if (r0 != r2) goto L_0x008d
            goto L_0x008e
        L_0x008d:
            r2 = 0
        L_0x008e:
            return r2
        L_0x008f:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x008f }
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.waitUntilOperationComplete(int):boolean");
    }

    public void handleCancel(int token, boolean cancelAll) {
        Operation op;
        synchronized (this.mCurrentOpLock) {
            op = this.mCurrentOperations.get(token);
            if (op == null) {
                Slog.w(BackupManagerService.TAG, "Cancel of token " + Integer.toHexString(token) + " but no op found");
            }
            int state = op != null ? op.state : -1;
            if (state == 1) {
                Slog.w(BackupManagerService.TAG, "Operation already got an ack.Should have been removed from mCurrentOperations.");
                op = null;
                this.mCurrentOperations.delete(token);
            } else if (state == 0) {
                Slog.v(BackupManagerService.TAG, "Cancel: token=" + Integer.toHexString(token));
                op.state = -1;
                if (op.type == 0 || op.type == 1) {
                    this.mBackupHandler.removeMessages(getMessageIdForOperationType(op.type));
                }
            }
            this.mCurrentOpLock.notifyAll();
        }
        if (op != null && op.callback != null) {
            Slog.v(BackupManagerService.TAG, "   Invoking cancel on " + op.callback);
            op.callback.handleCancel(cancelAll);
        }
    }

    public boolean isBackupOperationInProgress() {
        synchronized (this.mCurrentOpLock) {
            for (int i = 0; i < this.mCurrentOperations.size(); i++) {
                Operation op = this.mCurrentOperations.valueAt(i);
                if (op.type == 2 && op.state == 0) {
                    return true;
                }
            }
            return false;
        }
    }

    public void tearDownAgentAndKill(ApplicationInfo app) {
        if (app != null) {
            try {
                this.mActivityManager.unbindBackupAgent(app);
                if (UserHandle.isCore(app.uid) || app.packageName.equals("com.android.backupconfirm") || !BackupManagerServiceInjector.isNeedBeKilled(app.packageName, this.mCallerFd)) {
                    Slog.d(BackupManagerService.TAG, "Not killing after operation: " + app.processName);
                    return;
                }
                Slog.d(BackupManagerService.TAG, "Killing agent host process. app.processName:" + app.processName + " uid:" + app.uid);
                this.mActivityManager.killApplicationProcess(app.processName, app.uid);
            } catch (RemoteException e) {
                Slog.d(BackupManagerService.TAG, "Lost app trying to shut down");
            }
        }
    }

    public boolean deviceIsEncrypted() {
        try {
            if (this.mStorageManager.getEncryptionState() == 1 || this.mStorageManager.getPasswordType() == 1) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Slog.e(BackupManagerService.TAG, "Unable to communicate with storagemanager service: " + e.getMessage());
            return true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0048, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void scheduleNextFullBackupJob(long r19) {
        /*
            r18 = this;
            r1 = r18
            java.lang.Object r2 = r1.mQueueLock
            monitor-enter(r2)
            java.util.ArrayList<com.android.server.backup.fullbackup.FullBackupEntry> r0 = r1.mFullBackupQueue     // Catch:{ all -> 0x0049 }
            int r0 = r0.size()     // Catch:{ all -> 0x0049 }
            if (r0 <= 0) goto L_0x003e
            java.util.ArrayList<com.android.server.backup.fullbackup.FullBackupEntry> r0 = r1.mFullBackupQueue     // Catch:{ all -> 0x0049 }
            r3 = 0
            java.lang.Object r0 = r0.get(r3)     // Catch:{ all -> 0x0049 }
            com.android.server.backup.fullbackup.FullBackupEntry r0 = (com.android.server.backup.fullbackup.FullBackupEntry) r0     // Catch:{ all -> 0x0049 }
            long r3 = r0.lastBackup     // Catch:{ all -> 0x0049 }
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0049 }
            long r5 = r5 - r3
            com.android.server.backup.BackupManagerConstants r0 = r1.mConstants     // Catch:{ all -> 0x0049 }
            long r7 = r0.getFullBackupIntervalMilliseconds()     // Catch:{ all -> 0x0049 }
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 >= 0) goto L_0x002a
            long r9 = r7 - r5
            goto L_0x002c
        L_0x002a:
            r9 = 0
        L_0x002c:
            r11 = r19
            long r13 = java.lang.Math.max(r11, r9)     // Catch:{ all -> 0x004e }
            int r0 = r1.mUserId     // Catch:{ all -> 0x004e }
            android.content.Context r15 = r1.mContext     // Catch:{ all -> 0x004e }
            r16 = r3
            com.android.server.backup.BackupManagerConstants r3 = r1.mConstants     // Catch:{ all -> 0x004e }
            com.android.server.backup.FullBackupJob.schedule(r0, r15, r13, r3)     // Catch:{ all -> 0x004e }
            goto L_0x0047
        L_0x003e:
            r11 = r19
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r3 = "Full backup queue empty; not scheduling"
            android.util.Slog.i(r0, r3)     // Catch:{ all -> 0x004e }
        L_0x0047:
            monitor-exit(r2)     // Catch:{ all -> 0x004e }
            return
        L_0x0049:
            r0 = move-exception
            r11 = r19
        L_0x004c:
            monitor-exit(r2)     // Catch:{ all -> 0x004e }
            throw r0
        L_0x004e:
            r0 = move-exception
            goto L_0x004c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.scheduleNextFullBackupJob(long):void");
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mQueueLock"})
    public void dequeueFullBackupLocked(String packageName) {
        for (int i = this.mFullBackupQueue.size() - 1; i >= 0; i--) {
            if (packageName.equals(this.mFullBackupQueue.get(i).packageName)) {
                this.mFullBackupQueue.remove(i);
            }
        }
    }

    public void enqueueFullBackup(String packageName, long lastBackedUp) {
        FullBackupEntry newEntry = new FullBackupEntry(packageName, lastBackedUp);
        synchronized (this.mQueueLock) {
            dequeueFullBackupLocked(packageName);
            int which = -1;
            if (lastBackedUp > 0) {
                which = this.mFullBackupQueue.size() - 1;
                while (true) {
                    if (which < 0) {
                        break;
                    } else if (this.mFullBackupQueue.get(which).lastBackup <= lastBackedUp) {
                        this.mFullBackupQueue.add(which + 1, newEntry);
                        break;
                    } else {
                        which--;
                    }
                }
            }
            if (which < 0) {
                this.mFullBackupQueue.add(0, newEntry);
            }
        }
        writeFullBackupScheduleAsync();
    }

    private boolean fullBackupAllowable(String transportName) {
        if (!this.mTransportManager.isTransportRegistered(transportName)) {
            Slog.w(BackupManagerService.TAG, "Transport not registered; full data backup not performed");
            return false;
        }
        try {
            if (new File(new File(this.mBaseStateDir, this.mTransportManager.getTransportDirName(transportName)), PACKAGE_MANAGER_SENTINEL).length() > 0) {
                return true;
            }
            Slog.i(BackupManagerService.TAG, "Full backup requested but dataset not yet initialized");
            return false;
        } catch (Exception e) {
            Slog.w(BackupManagerService.TAG, "Unable to get transport name: " + e.getMessage());
            return false;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        android.util.Slog.i(com.android.server.backup.BackupManagerService.TAG, "Backup queue empty; doing nothing");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0074, code lost:
        r0 = false;
        r5 = r1;
        r1 = r2;
     */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x01a5  */
    /* JADX WARNING: Removed duplicated region for block: B:107:0x01a7  */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x0282 A[LOOP:0: B:27:0x0065->B:143:0x0282, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x01d7 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x011f A[SYNTHETIC, Splitter:B:82:0x011f] */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x0186  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean beginFullBackup(com.android.server.backup.FullBackupJob r29) {
        /*
            r28 = this;
            r11 = r28
            long r12 = java.lang.System.currentTimeMillis()
            com.android.server.backup.BackupManagerConstants r1 = r11.mConstants
            monitor-enter(r1)
            com.android.server.backup.BackupManagerConstants r0 = r11.mConstants     // Catch:{ all -> 0x02d1 }
            long r2 = r0.getFullBackupIntervalMilliseconds()     // Catch:{ all -> 0x02d1 }
            r14 = r2
            com.android.server.backup.BackupManagerConstants r0 = r11.mConstants     // Catch:{ all -> 0x02d1 }
            long r2 = r0.getKeyValueBackupIntervalMilliseconds()     // Catch:{ all -> 0x02d1 }
            r9 = r2
            monitor-exit(r1)     // Catch:{ all -> 0x02d1 }
            r1 = 0
            r2 = r14
            boolean r0 = r11.mEnabled
            r4 = 0
            if (r0 == 0) goto L_0x02a6
            boolean r0 = r11.mSetupComplete
            if (r0 != 0) goto L_0x0027
            r26 = r9
            goto L_0x02a8
        L_0x0027:
            android.os.PowerManager r0 = r11.mPowerManager
            r5 = 4
            android.os.PowerSaveState r8 = r0.getPowerSaveState(r5)
            boolean r0 = r8.batterySaverEnabled
            if (r0 == 0) goto L_0x0043
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r5 = "Deferring scheduled full backups in battery saver mode"
            android.util.Slog.i(r0, r5)
            int r0 = r11.mUserId
            android.content.Context r5 = r11.mContext
            com.android.server.backup.BackupManagerConstants r6 = r11.mConstants
            com.android.server.backup.FullBackupJob.schedule(r0, r5, r9, r6)
            return r4
        L_0x0043:
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r5 = "Beginning scheduled full backup operation"
            android.util.Slog.i(r0, r5)
            java.lang.Object r7 = r11.mQueueLock
            monitor-enter(r7)
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask r0 = r11.mRunningFullBackupTask     // Catch:{ all -> 0x029b }
            if (r0 == 0) goto L_0x0063
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r5 = "Backup triggered but one already/still running!"
            android.util.Slog.e(r0, r5)     // Catch:{ all -> 0x005a }
            monitor-exit(r7)     // Catch:{ all -> 0x005a }
            return r4
        L_0x005a:
            r0 = move-exception
            r25 = r7
            r16 = r8
            r26 = r9
            goto L_0x02a2
        L_0x0063:
            r5 = 1
            r0 = r5
        L_0x0065:
            java.util.ArrayList<com.android.server.backup.fullbackup.FullBackupEntry> r6 = r11.mFullBackupQueue     // Catch:{ all -> 0x0291 }
            int r6 = r6.size()     // Catch:{ all -> 0x0291 }
            if (r6 != 0) goto L_0x0079
            java.lang.String r6 = "BackupManagerService"
            java.lang.String r5 = "Backup queue empty; doing nothing"
            android.util.Slog.i(r6, r5)     // Catch:{ all -> 0x005a }
            r0 = 0
            r5 = r1
            r1 = r2
            goto L_0x01da
        L_0x0079:
            r5 = 0
            com.android.server.backup.TransportManager r6 = r11.mTransportManager     // Catch:{ all -> 0x0291 }
            java.lang.String r6 = r6.getCurrentTransportName()     // Catch:{ all -> 0x0291 }
            boolean r17 = r11.fullBackupAllowable(r6)     // Catch:{ all -> 0x0291 }
            if (r17 != 0) goto L_0x0092
            java.lang.String r4 = "BackupManagerService"
            r18 = r0
            java.lang.String r0 = "Preconditions not met; not running full backup"
            android.util.Slog.i(r4, r0)     // Catch:{ all -> 0x005a }
            r0 = 0
            r2 = r9
            goto L_0x0094
        L_0x0092:
            r18 = r0
        L_0x0094:
            if (r0 == 0) goto L_0x01cf
            java.util.ArrayList<com.android.server.backup.fullbackup.FullBackupEntry> r4 = r11.mFullBackupQueue     // Catch:{ all -> 0x01c2 }
            r18 = r1
            r1 = 0
            java.lang.Object r4 = r4.get(r1)     // Catch:{ all -> 0x01b5 }
            com.android.server.backup.fullbackup.FullBackupEntry r4 = (com.android.server.backup.fullbackup.FullBackupEntry) r4     // Catch:{ all -> 0x01b5 }
            r1 = r4
            r19 = r2
            long r2 = r1.lastBackup     // Catch:{ all -> 0x01aa }
            long r2 = r12 - r2
            int r4 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
            if (r4 < 0) goto L_0x00ae
            r4 = 1
            goto L_0x00af
        L_0x00ae:
            r4 = 0
        L_0x00af:
            if (r4 != 0) goto L_0x00c3
            java.lang.String r0 = "BackupManagerService"
            r18 = r4
            java.lang.String r4 = "Device ready but too early to back up next app"
            android.util.Slog.i(r0, r4)     // Catch:{ all -> 0x01aa }
            long r19 = r14 - r2
            r5 = r1
            r0 = r18
            r1 = r19
            goto L_0x01da
        L_0x00c3:
            r18 = r4
            android.content.pm.PackageManager r0 = r11.mPackageManager     // Catch:{ NameNotFoundException -> 0x0197 }
            java.lang.String r4 = r1.packageName     // Catch:{ NameNotFoundException -> 0x0197 }
            r21 = r2
            int r2 = r11.mUserId     // Catch:{ NameNotFoundException -> 0x0193 }
            r3 = 0
            android.content.pm.PackageInfo r0 = r0.getPackageInfoAsUser(r4, r3, r2)     // Catch:{ NameNotFoundException -> 0x0193 }
            boolean r2 = com.android.server.backup.utils.AppBackupUtils.appGetsFullBackup(r0)     // Catch:{ NameNotFoundException -> 0x0193 }
            if (r2 != 0) goto L_0x0105
            java.lang.String r2 = "BackupManagerService"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x0100 }
            r3.<init>()     // Catch:{ NameNotFoundException -> 0x0100 }
            java.lang.String r4 = "Culling package "
            r3.append(r4)     // Catch:{ NameNotFoundException -> 0x0100 }
            java.lang.String r4 = r1.packageName     // Catch:{ NameNotFoundException -> 0x0100 }
            r3.append(r4)     // Catch:{ NameNotFoundException -> 0x0100 }
            java.lang.String r4 = " in full-backup queue but not eligible"
            r3.append(r4)     // Catch:{ NameNotFoundException -> 0x0100 }
            java.lang.String r3 = r3.toString()     // Catch:{ NameNotFoundException -> 0x0100 }
            android.util.Slog.i(r2, r3)     // Catch:{ NameNotFoundException -> 0x0100 }
            java.util.ArrayList<com.android.server.backup.fullbackup.FullBackupEntry> r2 = r11.mFullBackupQueue     // Catch:{ NameNotFoundException -> 0x0100 }
            r3 = 0
            r2.remove(r3)     // Catch:{ NameNotFoundException -> 0x0100 }
            r5 = 1
            r0 = r18
            goto L_0x01d5
        L_0x0100:
            r0 = move-exception
            r25 = r6
            goto L_0x019c
        L_0x0105:
            android.content.pm.ApplicationInfo r2 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x0193 }
            int r2 = r2.privateFlags     // Catch:{ NameNotFoundException -> 0x0193 }
            r3 = r2 & 8192(0x2000, float:1.14794E-41)
            if (r3 != 0) goto L_0x011b
            android.app.ActivityManagerInternal r3 = r11.mActivityManagerInternal     // Catch:{ NameNotFoundException -> 0x0100 }
            android.content.pm.ApplicationInfo r4 = r0.applicationInfo     // Catch:{ NameNotFoundException -> 0x0100 }
            int r4 = r4.uid     // Catch:{ NameNotFoundException -> 0x0100 }
            boolean r3 = r3.isAppForeground(r4)     // Catch:{ NameNotFoundException -> 0x0100 }
            if (r3 == 0) goto L_0x011b
            r3 = 1
            goto L_0x011c
        L_0x011b:
            r3 = 0
        L_0x011c:
            r5 = r3
            if (r5 == 0) goto L_0x0186
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ NameNotFoundException -> 0x0180 }
            r23 = 3600000(0x36ee80, double:1.7786363E-317)
            long r3 = r3 + r23
            r23 = r0
            java.util.Random r0 = r11.mTokenGenerator     // Catch:{ NameNotFoundException -> 0x0180 }
            r24 = r2
            r2 = 7200000(0x6ddd00, float:1.0089349E-38)
            int r0 = r0.nextInt(r2)     // Catch:{ NameNotFoundException -> 0x0180 }
            r2 = r5
            r25 = r6
            long r5 = (long) r0
            long r3 = r3 + r5
            java.text.SimpleDateFormat r0 = new java.text.SimpleDateFormat     // Catch:{ NameNotFoundException -> 0x017a }
            java.lang.String r5 = "yyyy-MM-dd HH:mm:ss"
            r0.<init>(r5)     // Catch:{ NameNotFoundException -> 0x017a }
            java.lang.String r5 = "BackupManagerService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ NameNotFoundException -> 0x017a }
            r6.<init>()     // Catch:{ NameNotFoundException -> 0x017a }
            r26 = r2
            java.lang.String r2 = "Full backup time but "
            r6.append(r2)     // Catch:{ NameNotFoundException -> 0x0176 }
            java.lang.String r2 = r1.packageName     // Catch:{ NameNotFoundException -> 0x0176 }
            r6.append(r2)     // Catch:{ NameNotFoundException -> 0x0176 }
            java.lang.String r2 = " is busy; deferring to "
            r6.append(r2)     // Catch:{ NameNotFoundException -> 0x0176 }
            java.util.Date r2 = new java.util.Date     // Catch:{ NameNotFoundException -> 0x0176 }
            r2.<init>(r3)     // Catch:{ NameNotFoundException -> 0x0176 }
            java.lang.String r2 = r0.format(r2)     // Catch:{ NameNotFoundException -> 0x0176 }
            r6.append(r2)     // Catch:{ NameNotFoundException -> 0x0176 }
            java.lang.String r2 = r6.toString()     // Catch:{ NameNotFoundException -> 0x0176 }
            android.util.Slog.i(r5, r2)     // Catch:{ NameNotFoundException -> 0x0176 }
            java.lang.String r0 = r1.packageName     // Catch:{ NameNotFoundException -> 0x0176 }
            long r5 = r3 - r14
            r11.enqueueFullBackup(r0, r5)     // Catch:{ NameNotFoundException -> 0x0176 }
            goto L_0x018e
        L_0x0176:
            r0 = move-exception
            r5 = r26
            goto L_0x019c
        L_0x017a:
            r0 = move-exception
            r26 = r2
            r5 = r26
            goto L_0x019c
        L_0x0180:
            r0 = move-exception
            r26 = r5
            r25 = r6
            goto L_0x019c
        L_0x0186:
            r23 = r0
            r24 = r2
            r26 = r5
            r25 = r6
        L_0x018e:
            r0 = r18
            r5 = r26
            goto L_0x01d5
        L_0x0193:
            r0 = move-exception
            r25 = r6
            goto L_0x019c
        L_0x0197:
            r0 = move-exception
            r21 = r2
            r25 = r6
        L_0x019c:
            java.util.ArrayList<com.android.server.backup.fullbackup.FullBackupEntry> r2 = r11.mFullBackupQueue     // Catch:{ all -> 0x01aa }
            int r2 = r2.size()     // Catch:{ all -> 0x01aa }
            r3 = 1
            if (r2 <= r3) goto L_0x01a7
            r2 = 1
            goto L_0x01a8
        L_0x01a7:
            r2 = 0
        L_0x01a8:
            r0 = r2
            goto L_0x01d5
        L_0x01aa:
            r0 = move-exception
            r25 = r7
            r16 = r8
            r26 = r9
            r2 = r19
            goto L_0x02a2
        L_0x01b5:
            r0 = move-exception
            r19 = r2
            r25 = r7
            r16 = r8
            r26 = r9
            r1 = r18
            goto L_0x02a2
        L_0x01c2:
            r0 = move-exception
            r18 = r1
            r19 = r2
            r25 = r7
            r16 = r8
            r26 = r9
            goto L_0x02a2
        L_0x01cf:
            r18 = r1
            r19 = r2
            r25 = r6
        L_0x01d5:
            if (r5 != 0) goto L_0x0282
            r5 = r1
            r1 = r19
        L_0x01da:
            if (r0 != 0) goto L_0x021d
            java.lang.String r3 = "BackupManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x020f }
            r4.<init>()     // Catch:{ all -> 0x020f }
            java.lang.String r6 = "Nothing pending full backup; rescheduling +"
            r4.append(r6)     // Catch:{ all -> 0x020f }
            r4.append(r1)     // Catch:{ all -> 0x020f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x020f }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x020f }
            r3 = r1
            int r6 = r11.mUserId     // Catch:{ all -> 0x020f }
            r18 = r0
            android.content.Context r0 = r11.mContext     // Catch:{ all -> 0x020f }
            r19 = r1
            com.android.server.backup.BackupManagerConstants r1 = r11.mConstants     // Catch:{ all -> 0x0203 }
            com.android.server.backup.FullBackupJob.schedule(r6, r0, r3, r1)     // Catch:{ all -> 0x0203 }
            monitor-exit(r7)     // Catch:{ all -> 0x0203 }
            r1 = 0
            return r1
        L_0x0203:
            r0 = move-exception
            r1 = r5
            r25 = r7
            r16 = r8
            r26 = r9
            r2 = r19
            goto L_0x02a2
        L_0x020f:
            r0 = move-exception
            r19 = r1
            r1 = r5
            r25 = r7
            r16 = r8
            r26 = r9
            r2 = r19
            goto L_0x02a2
        L_0x021d:
            r18 = r0
            r19 = r1
            java.util.ArrayList<com.android.server.backup.fullbackup.FullBackupEntry> r0 = r11.mFullBackupQueue     // Catch:{ all -> 0x0274 }
            r1 = 0
            r0.remove(r1)     // Catch:{ all -> 0x0274 }
            java.util.concurrent.CountDownLatch r6 = new java.util.concurrent.CountDownLatch     // Catch:{ all -> 0x0274 }
            r4 = 1
            r6.<init>(r4)     // Catch:{ all -> 0x0274 }
            java.lang.String[] r3 = new java.lang.String[r4]     // Catch:{ all -> 0x0274 }
            java.lang.String r0 = r5.packageName     // Catch:{ all -> 0x0274 }
            r1 = 0
            r3[r1] = r0     // Catch:{ all -> 0x0274 }
            r2 = 0
            r0 = 1
            r16 = 0
            r17 = 0
            r21 = 0
            java.lang.String r22 = "BMS.beginFullBackup()"
            r1 = r28
            r23 = r4
            r4 = r0
            r24 = r23
            r23 = r5
            r5 = r29
            r25 = r7
            r7 = r16
            r16 = r8
            r8 = r17
            r26 = r9
            r9 = r21
            r10 = r22
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask r0 = com.android.server.backup.fullbackup.PerformFullTransportBackupTask.newWithCurrentTransport(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x026e }
            r11.mRunningFullBackupTask = r0     // Catch:{ all -> 0x026e }
            android.os.PowerManager$WakeLock r0 = r11.mWakelock     // Catch:{ all -> 0x026e }
            r0.acquire()     // Catch:{ all -> 0x026e }
            java.lang.Thread r0 = new java.lang.Thread     // Catch:{ all -> 0x026e }
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask r1 = r11.mRunningFullBackupTask     // Catch:{ all -> 0x026e }
            r0.<init>(r1)     // Catch:{ all -> 0x026e }
            r0.start()     // Catch:{ all -> 0x026e }
            monitor-exit(r25)     // Catch:{ all -> 0x026e }
            return r24
        L_0x026e:
            r0 = move-exception
            r2 = r19
            r1 = r23
            goto L_0x02a2
        L_0x0274:
            r0 = move-exception
            r23 = r5
            r25 = r7
            r16 = r8
            r26 = r9
            r2 = r19
            r1 = r23
            goto L_0x02a2
        L_0x0282:
            r25 = r7
            r16 = r8
            r26 = r9
            r24 = 1
            r2 = r19
            r5 = r24
            r4 = 0
            goto L_0x0065
        L_0x0291:
            r0 = move-exception
            r18 = r1
            r25 = r7
            r16 = r8
            r26 = r9
            goto L_0x02a2
        L_0x029b:
            r0 = move-exception
            r25 = r7
            r16 = r8
            r26 = r9
        L_0x02a2:
            monitor-exit(r25)     // Catch:{ all -> 0x02a4 }
            throw r0
        L_0x02a4:
            r0 = move-exception
            goto L_0x02a2
        L_0x02a6:
            r26 = r9
        L_0x02a8:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "beginFullBackup but enabled="
            r0.append(r4)
            boolean r4 = r11.mEnabled
            r0.append(r4)
            java.lang.String r4 = " setupComplete="
            r0.append(r4)
            boolean r4 = r11.mSetupComplete
            r0.append(r4)
            java.lang.String r4 = "; ignoring"
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "BackupManagerService"
            android.util.Slog.i(r4, r0)
            r4 = 0
            return r4
        L_0x02d1:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x02d1 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.beginFullBackup(com.android.server.backup.FullBackupJob):boolean");
    }

    public void endFullBackup() {
        new Thread(new Runnable() {
            public void run() {
                PerformFullTransportBackupTask pftbt = null;
                synchronized (UserBackupManagerService.this.mQueueLock) {
                    if (UserBackupManagerService.this.mRunningFullBackupTask != null) {
                        pftbt = UserBackupManagerService.this.mRunningFullBackupTask;
                    }
                }
                if (pftbt != null) {
                    Slog.i(BackupManagerService.TAG, "Telling running backup to stop");
                    pftbt.handleCancel(true);
                }
            }
        }, "end-full-backup").start();
    }

    public void restoreWidgetData(String packageName, byte[] widgetData) {
        Slog.i(BackupManagerService.TAG, "Incorporating restored widget data");
        AppWidgetBackupBridge.restoreWidgetState(packageName, widgetData, this.mUserId);
    }

    public void dataChangedImpl(String packageName) {
        dataChangedImpl(packageName, dataChangedTargets(packageName));
    }

    /* access modifiers changed from: private */
    public void dataChangedImpl(String packageName, HashSet<String> targets) {
        if (targets == null) {
            Slog.w(BackupManagerService.TAG, "dataChanged but no participant pkg='" + packageName + "' uid=" + Binder.getCallingUid());
            return;
        }
        synchronized (this.mQueueLock) {
            if (targets.contains(packageName)) {
                if (this.mPendingBackups.put(packageName, new BackupRequest(packageName)) == null) {
                    Slog.d(BackupManagerService.TAG, "Now staging backup of " + packageName);
                    writeToJournalLocked(packageName);
                }
            }
        }
        KeyValueBackupJob.schedule(this.mUserId, this.mContext, this.mConstants);
    }

    private HashSet<String> dataChangedTargets(String packageName) {
        HashSet<String> union;
        HashSet<String> hashSet;
        if (this.mContext.checkPermission("android.permission.BACKUP", Binder.getCallingPid(), Binder.getCallingUid()) == -1) {
            synchronized (this.mBackupParticipants) {
                hashSet = this.mBackupParticipants.get(Binder.getCallingUid());
            }
            return hashSet;
        } else if (PACKAGE_MANAGER_SENTINEL.equals(packageName)) {
            return Sets.newHashSet(new String[]{PACKAGE_MANAGER_SENTINEL});
        } else {
            synchronized (this.mBackupParticipants) {
                union = SparseArrayUtils.union(this.mBackupParticipants);
            }
            return union;
        }
    }

    private void writeToJournalLocked(String str) {
        try {
            if (this.mJournal == null) {
                this.mJournal = DataChangedJournal.newJournal(this.mJournalDir);
            }
            this.mJournal.addPackage(str);
        } catch (IOException e) {
            Slog.e(BackupManagerService.TAG, "Can't write " + str + " to backup journal", e);
            this.mJournal = null;
        }
    }

    public void dataChanged(final String packageName) {
        final HashSet<String> targets = dataChangedTargets(packageName);
        if (targets == null) {
            Slog.w(BackupManagerService.TAG, "dataChanged but no participant pkg='" + packageName + "' uid=" + Binder.getCallingUid());
            return;
        }
        this.mBackupHandler.post(new Runnable() {
            public void run() {
                UserBackupManagerService.this.dataChangedImpl(packageName, targets);
            }
        });
    }

    public void initializeTransports(String[] transportNames, IBackupObserver observer) {
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "initializeTransport");
        Slog.v(BackupManagerService.TAG, "initializeTransport(): " + Arrays.asList(transportNames));
        long oldId = Binder.clearCallingIdentity();
        try {
            this.mWakelock.acquire();
            this.mBackupHandler.post(new PerformInitializeTask(this, transportNames, observer, new OnTaskFinishedListener() {
                public final void onFinished(String str) {
                    UserBackupManagerService.this.lambda$initializeTransports$2$UserBackupManagerService(str);
                }
            }));
        } finally {
            Binder.restoreCallingIdentity(oldId);
        }
    }

    public /* synthetic */ void lambda$initializeTransports$2$UserBackupManagerService(String caller) {
        this.mWakelock.release();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002e, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002f, code lost:
        if (r0 != null) goto L_0x0031;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        $closeResource(r2, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0034, code lost:
        throw r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setAncestralSerialNumber(long r5) {
        /*
            r4 = this;
            android.content.Context r0 = r4.mContext
            java.lang.String r1 = "android.permission.BACKUP"
            java.lang.String r2 = "setAncestralSerialNumber"
            r0.enforceCallingPermission(r1, r2)
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Setting ancestral work profile id to "
            r0.append(r1)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "BackupManagerService"
            android.util.Slog.v(r1, r0)
            java.io.RandomAccessFile r0 = r4.getAncestralSerialNumberFile()     // Catch:{ IOException -> 0x0035 }
            r2 = 0
            r0.writeLong(r5)     // Catch:{ all -> 0x002c }
            $closeResource(r2, r0)     // Catch:{ IOException -> 0x0035 }
            goto L_0x003b
        L_0x002c:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x002e }
        L_0x002e:
            r3 = move-exception
            if (r0 == 0) goto L_0x0034
            $closeResource(r2, r0)     // Catch:{ IOException -> 0x0035 }
        L_0x0034:
            throw r3     // Catch:{ IOException -> 0x0035 }
        L_0x0035:
            r0 = move-exception
            java.lang.String r2 = "Unable to write to work profile serial mapping file:"
            android.util.Slog.w(r1, r2, r0)
        L_0x003b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.setAncestralSerialNumber(long):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x000f, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0010, code lost:
        if (r0 != null) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        $closeResource(r1, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0015, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getAncestralSerialNumber() {
        /*
            r4 = this;
            java.io.RandomAccessFile r0 = r4.getAncestralSerialNumberFile()     // Catch:{ IOException -> 0x0016 }
            r1 = 0
            long r2 = r0.readLong()     // Catch:{ all -> 0x000d }
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0016 }
            return r2
        L_0x000d:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x000f }
        L_0x000f:
            r2 = move-exception
            if (r0 == 0) goto L_0x0015
            $closeResource(r1, r0)     // Catch:{ IOException -> 0x0016 }
        L_0x0015:
            throw r2     // Catch:{ IOException -> 0x0016 }
        L_0x0016:
            r0 = move-exception
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r2 = "Unable to write to work profile serial number file:"
            android.util.Slog.w(r1, r2, r0)
            r1 = -1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.UserBackupManagerService.getAncestralSerialNumber():long");
    }

    private RandomAccessFile getAncestralSerialNumberFile() throws FileNotFoundException {
        if (this.mAncestralSerialNumberFile == null) {
            this.mAncestralSerialNumberFile = new File(UserBackupManagerFiles.getBaseStateDir(getUserId()), SERIAL_ID_FILE);
            FileUtils.createNewFile(this.mAncestralSerialNumberFile);
        }
        return new RandomAccessFile(this.mAncestralSerialNumberFile, "rwd");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setAncestralSerialNumberFile(File ancestralSerialNumberFile) {
        this.mAncestralSerialNumberFile = ancestralSerialNumberFile;
    }

    public void clearBackupData(String transportName, String packageName) {
        Set<String> apps;
        Slog.v(BackupManagerService.TAG, "clearBackupData() of " + packageName + " on " + transportName);
        try {
            PackageInfo info = this.mPackageManager.getPackageInfoAsUser(packageName, 134217728, this.mUserId);
            if (this.mContext.checkPermission("android.permission.BACKUP", Binder.getCallingPid(), Binder.getCallingUid()) == -1) {
                apps = this.mBackupParticipants.get(Binder.getCallingUid());
            } else {
                Slog.v(BackupManagerService.TAG, "Privileged caller, allowing clear of other apps");
                apps = this.mProcessedPackagesJournal.getPackagesCopy();
            }
            if (apps.contains(packageName)) {
                Slog.v(BackupManagerService.TAG, "Found the app - running clear process");
                this.mBackupHandler.removeMessages(12);
                synchronized (this.mQueueLock) {
                    TransportClient transportClient = this.mTransportManager.getTransportClient(transportName, "BMS.clearBackupData()");
                    if (transportClient == null) {
                        this.mBackupHandler.sendMessageDelayed(this.mBackupHandler.obtainMessage(12, new ClearRetryParams(transportName, packageName)), 3600000);
                        return;
                    }
                    long oldId = Binder.clearCallingIdentity();
                    OnTaskFinishedListener listener = new OnTaskFinishedListener(transportClient) {
                        private final /* synthetic */ TransportClient f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void onFinished(String str) {
                            UserBackupManagerService.this.lambda$clearBackupData$3$UserBackupManagerService(this.f$1, str);
                        }
                    };
                    this.mWakelock.acquire();
                    this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(4, new ClearParams(transportClient, info, listener)));
                    Binder.restoreCallingIdentity(oldId);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            Slog.d(BackupManagerService.TAG, "No such package '" + packageName + "' - not clearing backup data");
        }
    }

    public /* synthetic */ void lambda$clearBackupData$3$UserBackupManagerService(TransportClient transportClient, String caller) {
        this.mTransportManager.disposeOfTransportClient(transportClient, caller);
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void backupNow() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "backupNow");
        long oldId = Binder.clearCallingIdentity();
        try {
            if (this.mPowerManager.getPowerSaveState(5).batterySaverEnabled) {
                Slog.v(BackupManagerService.TAG, "Not running backup while in battery save mode");
                KeyValueBackupJob.schedule(this.mUserId, this.mContext, this.mConstants);
            } else {
                Slog.v(BackupManagerService.TAG, "Scheduling immediate backup pass");
                synchronized (this.mQueueLock) {
                    try {
                        this.mRunBackupIntent.send();
                    } catch (PendingIntent.CanceledException e) {
                        Slog.e(BackupManagerService.TAG, "run-backup intent cancelled!");
                    }
                    KeyValueBackupJob.cancel(this.mUserId, this.mContext);
                }
            }
            Binder.restoreCallingIdentity(oldId);
        } catch (Throwable result) {
            Binder.restoreCallingIdentity(oldId);
            throw result;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    public void adbBackup(ParcelFileDescriptor fd, boolean includeApks, boolean includeObbs, boolean includeShared, boolean doWidgets, boolean doAllApps, boolean includeSystem, boolean compress, boolean doKeyValue, String[] pkgList) {
        int i;
        Throwable th;
        boolean z = includeShared;
        boolean z2 = doAllApps;
        String[] strArr = pkgList;
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "adbBackup");
        if (UserHandle.getCallingUserId() != 0) {
            throw new IllegalStateException("Backup supported only for the device owner");
        } else if (z2 || z || !(strArr == null || strArr.length == 0)) {
            long oldId = Binder.clearCallingIdentity();
            try {
                if (!this.mSetupComplete) {
                    try {
                        Slog.i(BackupManagerService.TAG, "Backup not supported before setup");
                        this.mCallerFd = 0;
                        try {
                            fd.close();
                        } catch (IOException e) {
                            Slog.e(BackupManagerService.TAG, "IO error closing output for adb backup: " + e.getMessage());
                        }
                        Binder.restoreCallingIdentity(oldId);
                        Slog.d(BackupManagerService.TAG, "Adb backup processing complete.");
                    } catch (Throwable th2) {
                        th = th2;
                        i = 0;
                        this.mCallerFd = i;
                        try {
                            fd.close();
                        } catch (IOException e2) {
                            Slog.e(BackupManagerService.TAG, "IO error closing output for adb backup: " + e2.getMessage());
                        }
                        Binder.restoreCallingIdentity(oldId);
                        Slog.d(BackupManagerService.TAG, "Adb backup processing complete.");
                        throw th;
                    }
                } else {
                    Slog.v(BackupManagerService.TAG, "Requesting backup: apks=" + includeApks + " obb=" + includeObbs + " shared=" + z + " all=" + z2 + " system=" + includeSystem + " includekeyvalue=" + doKeyValue + " pkgs=" + strArr);
                    Slog.i(BackupManagerService.TAG, "Beginning adb backup...");
                    i = 0;
                    try {
                        AdbBackupParams adbBackupParams = new AdbBackupParams(fd, includeApks, includeObbs, includeShared, doWidgets, doAllApps, includeSystem, compress, doKeyValue, pkgList);
                        AdbBackupParams params = adbBackupParams;
                        int token = generateRandomIntegerToken();
                        synchronized (this.mAdbBackupRestoreConfirmations) {
                            this.mAdbBackupRestoreConfirmations.put(token, params);
                        }
                        Slog.d(BackupManagerService.TAG, "Starting backup confirmation UI, token=" + token);
                        this.mCallerFd = fd.getFd();
                        if (!BackupManagerServiceInjector.startConfirmationUi(this, token, "fullback", this.mCallerFd)) {
                            Slog.e(BackupManagerService.TAG, "Unable to launch backup confirmation UI");
                            this.mAdbBackupRestoreConfirmations.delete(token);
                            this.mCallerFd = 0;
                            try {
                                fd.close();
                            } catch (IOException e3) {
                                Slog.e(BackupManagerService.TAG, "IO error closing output for adb backup: " + e3.getMessage());
                            }
                            Binder.restoreCallingIdentity(oldId);
                            Slog.d(BackupManagerService.TAG, "Adb backup processing complete.");
                            return;
                        }
                        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
                        startConfirmationTimeout(token, params);
                        Slog.d(BackupManagerService.TAG, "Waiting for backup completion...");
                        waitForCompletion(params);
                        this.mCallerFd = 0;
                        try {
                            fd.close();
                        } catch (IOException e4) {
                            Slog.e(BackupManagerService.TAG, "IO error closing output for adb backup: " + e4.getMessage());
                        }
                        Binder.restoreCallingIdentity(oldId);
                        Slog.d(BackupManagerService.TAG, "Adb backup processing complete.");
                    } catch (Exception e5) {
                        Slog.e(BackupManagerService.TAG, "MIUI FD has been closed, " + e5);
                        this.mCallerFd = 0;
                        try {
                            fd.close();
                        } catch (IOException e6) {
                            Slog.e(BackupManagerService.TAG, "IO error closing output for adb backup: " + e6.getMessage());
                        }
                        Binder.restoreCallingIdentity(oldId);
                        Slog.d(BackupManagerService.TAG, "Adb backup processing complete.");
                    } catch (Throwable th3) {
                        th = th3;
                    }
                }
            } catch (Throwable th4) {
                th = th4;
                i = 0;
                th = th;
                this.mCallerFd = i;
                fd.close();
                Binder.restoreCallingIdentity(oldId);
                Slog.d(BackupManagerService.TAG, "Adb backup processing complete.");
                throw th;
            }
        } else {
            throw new IllegalArgumentException("Backup requested but neither shared nor any apps named");
        }
    }

    public void fullTransportBackup(String[] pkgNames) {
        String[] strArr = pkgNames;
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "fullTransportBackup");
        if (UserHandle.getCallingUserId() == 0) {
            if (!fullBackupAllowable(this.mTransportManager.getCurrentTransportName())) {
                Slog.i(BackupManagerService.TAG, "Full backup not currently possible -- key/value backup not yet run?");
            } else {
                Slog.d(BackupManagerService.TAG, "fullTransportBackup()");
                long oldId = Binder.clearCallingIdentity();
                try {
                    CountDownLatch latch = new CountDownLatch(1);
                    Runnable task = PerformFullTransportBackupTask.newWithCurrentTransport(this, (IFullBackupRestoreObserver) null, pkgNames, false, (FullBackupJob) null, latch, (IBackupObserver) null, (IBackupManagerMonitor) null, false, "BMS.fullTransportBackup()");
                    this.mWakelock.acquire();
                    new Thread(task, "full-transport-master").start();
                    while (true) {
                        try {
                            latch.await();
                            break;
                        } catch (InterruptedException e) {
                        }
                    }
                    long now = System.currentTimeMillis();
                    for (String pkg : strArr) {
                        enqueueFullBackup(pkg, now);
                    }
                } finally {
                    Binder.restoreCallingIdentity(oldId);
                }
            }
            Slog.d(BackupManagerService.TAG, "Done with full transport backup.");
            return;
        }
        throw new IllegalStateException("Restore supported only for the device owner");
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void adbRestore(ParcelFileDescriptor fd) {
        this.mContext.enforceCallingPermission("android.permission.BACKUP", "adbRestore");
        if (UserHandle.getCallingUserId() == 0) {
            long oldId = Binder.clearCallingIdentity();
            try {
                if (!this.mSetupComplete) {
                    Slog.i(BackupManagerService.TAG, "Full restore not permitted before setup");
                    this.mCallerFd = 0;
                    try {
                        fd.close();
                    } catch (IOException e) {
                        Slog.w(BackupManagerService.TAG, "Error trying to close fd after adb restore: " + e);
                    }
                    Binder.restoreCallingIdentity(oldId);
                    Slog.i(BackupManagerService.TAG, "adb restore processing complete.");
                    return;
                }
                Slog.i(BackupManagerService.TAG, "Beginning restore...");
                AdbRestoreParams params = new AdbRestoreParams(fd);
                int token = generateRandomIntegerToken();
                synchronized (this.mAdbBackupRestoreConfirmations) {
                    this.mAdbBackupRestoreConfirmations.put(token, params);
                }
                Slog.d(BackupManagerService.TAG, "Starting restore confirmation UI, token=" + token);
                this.mCallerFd = fd.getFd();
                if (!BackupManagerServiceInjector.startConfirmationUi(this, token, "fullrest", this.mCallerFd)) {
                    Slog.e(BackupManagerService.TAG, "Unable to launch restore confirmation");
                    this.mAdbBackupRestoreConfirmations.delete(token);
                    this.mCallerFd = 0;
                    try {
                        fd.close();
                    } catch (IOException e2) {
                        Slog.w(BackupManagerService.TAG, "Error trying to close fd after adb restore: " + e2);
                    }
                    Binder.restoreCallingIdentity(oldId);
                    Slog.i(BackupManagerService.TAG, "adb restore processing complete.");
                    return;
                }
                this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 0, 0);
                startConfirmationTimeout(token, params);
                Slog.d(BackupManagerService.TAG, "Waiting for restore completion...");
                waitForCompletion(params);
                this.mCallerFd = 0;
                try {
                    fd.close();
                } catch (IOException e3) {
                    Slog.w(BackupManagerService.TAG, "Error trying to close fd after adb restore: " + e3);
                }
                Binder.restoreCallingIdentity(oldId);
                Slog.i(BackupManagerService.TAG, "adb restore processing complete.");
            } catch (Exception e4) {
                Slog.e(BackupManagerService.TAG, "MIUI FD has been closed, " + e4);
                this.mCallerFd = 0;
                try {
                    fd.close();
                } catch (IOException e5) {
                    Slog.w(BackupManagerService.TAG, "Error trying to close fd after adb restore: " + e5);
                }
                Binder.restoreCallingIdentity(oldId);
                Slog.i(BackupManagerService.TAG, "adb restore processing complete.");
            } catch (Throwable th) {
                this.mCallerFd = 0;
                try {
                    fd.close();
                } catch (IOException e6) {
                    Slog.w(BackupManagerService.TAG, "Error trying to close fd after adb restore: " + e6);
                }
                Binder.restoreCallingIdentity(oldId);
                Slog.i(BackupManagerService.TAG, "adb restore processing complete.");
                throw th;
            }
        } else {
            throw new IllegalStateException("Restore supported only for the device owner");
        }
    }

    public boolean startConfirmationUi(int token, String action) {
        try {
            Intent confIntent = new Intent(action);
            confIntent.setClassName("com.android.backupconfirm", "com.android.backupconfirm.BackupRestoreConfirmation");
            confIntent.putExtra("conftoken", token);
            confIntent.addFlags(536870912);
            this.mContext.startActivityAsUser(confIntent, UserHandle.SYSTEM);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    private void startConfirmationTimeout(int token, AdbParams params) {
        Slog.d(BackupManagerService.TAG, "Posting conf timeout msg after 60000 millis");
        this.mBackupHandler.sendMessageDelayed(this.mBackupHandler.obtainMessage(9, token, 0, params), 60000);
    }

    private void waitForCompletion(AdbParams params) {
        synchronized (params.latch) {
            while (!params.latch.get()) {
                try {
                    params.latch.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

    public void signalAdbBackupRestoreCompletion(AdbParams params) {
        synchronized (params.latch) {
            params.latch.set(true);
            params.latch.notifyAll();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void acknowledgeAdbBackupOrRestore(int token, boolean allow, String curPassword, String encPpassword, IFullBackupRestoreObserver observer) {
        int verb;
        Slog.d(BackupManagerService.TAG, "acknowledgeAdbBackupOrRestore : token=" + token + " allow=" + allow);
        if (Binder.getCallingUid() != 1000) {
            this.mContext.enforceCallingPermission("android.permission.BACKUP", "acknowledgeAdbBackupOrRestore");
        }
        long oldId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mAdbBackupRestoreConfirmations) {
                AdbParams params = this.mAdbBackupRestoreConfirmations.get(token);
                if (params != null) {
                    this.mBackupHandler.removeMessages(9, params);
                    this.mAdbBackupRestoreConfirmations.delete(token);
                    if (allow) {
                        if (params instanceof AdbBackupParams) {
                            verb = 2;
                        } else {
                            verb = 10;
                        }
                        params.observer = observer;
                        params.curPassword = curPassword;
                        params.encryptPassword = encPpassword;
                        Slog.d(BackupManagerService.TAG, "Sending conf message with verb " + verb);
                        this.mWakelock.acquire();
                        this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(verb, params));
                    } else {
                        Slog.w(BackupManagerService.TAG, "User rejected full backup/restore operation");
                        signalAdbBackupRestoreCompletion(params);
                    }
                } else {
                    Slog.w(BackupManagerService.TAG, "Attempted to ack full backup/restore with invalid token");
                }
            }
            Binder.restoreCallingIdentity(oldId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(oldId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    public void setBackupEnabled(boolean enable) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "setBackupEnabled");
        Slog.i(BackupManagerService.TAG, "Backup enabled => " + enable);
        long oldId = Binder.clearCallingIdentity();
        try {
            boolean wasEnabled = this.mEnabled;
            synchronized (this) {
                UserBackupManagerFilePersistedSettings.writeBackupEnableState(this.mUserId, enable);
                this.mEnabled = enable;
            }
            synchronized (this.mQueueLock) {
                if (enable && !wasEnabled) {
                    if (this.mSetupComplete) {
                        KeyValueBackupJob.schedule(this.mUserId, this.mContext, this.mConstants);
                        scheduleNextFullBackupJob(0);
                    }
                }
                if (!enable) {
                    Slog.i(BackupManagerService.TAG, "Opting out of backup");
                    KeyValueBackupJob.cancel(this.mUserId, this.mContext);
                    if (wasEnabled && this.mSetupComplete) {
                        List<String> transportNames = new ArrayList<>();
                        List<String> transportDirNames = new ArrayList<>();
                        this.mTransportManager.forEachRegisteredTransport(new Consumer(transportNames, transportDirNames) {
                            private final /* synthetic */ List f$1;
                            private final /* synthetic */ List f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void accept(Object obj) {
                                UserBackupManagerService.this.lambda$setBackupEnabled$4$UserBackupManagerService(this.f$1, this.f$2, (String) obj);
                            }
                        });
                        for (int i = 0; i < transportNames.size(); i++) {
                            recordInitPending(true, transportNames.get(i), transportDirNames.get(i));
                        }
                        this.mAlarmManager.set(0, System.currentTimeMillis(), this.mRunInitIntent);
                    }
                }
            }
            Binder.restoreCallingIdentity(oldId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(oldId);
            throw th;
        }
    }

    public /* synthetic */ void lambda$setBackupEnabled$4$UserBackupManagerService(List transportNames, List transportDirNames, String name) {
        try {
            String dirName = this.mTransportManager.getTransportDirName(name);
            transportNames.add(name);
            transportDirNames.add(dirName);
        } catch (TransportNotRegisteredException e) {
            Slog.e(BackupManagerService.TAG, "Unexpected unregistered transport", e);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void setAutoRestore(boolean doAutoRestore) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "setAutoRestore");
        Slog.i(BackupManagerService.TAG, "Auto restore => " + doAutoRestore);
        long oldId = Binder.clearCallingIdentity();
        try {
            synchronized (this) {
                Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "backup_auto_restore", doAutoRestore ? 1 : 0, this.mUserId);
                this.mAutoRestore = doAutoRestore;
            }
            Binder.restoreCallingIdentity(oldId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(oldId);
            throw th;
        }
    }

    public boolean isBackupEnabled() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "isBackupEnabled");
        return this.mEnabled;
    }

    public String getCurrentTransport() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getCurrentTransport");
        String currentTransport = this.mTransportManager.getCurrentTransportName();
        Slog.v(BackupManagerService.TAG, "... getCurrentTransport() returning " + currentTransport);
        return currentTransport;
    }

    public ComponentName getCurrentTransportComponent() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getCurrentTransportComponent");
        long oldId = Binder.clearCallingIdentity();
        try {
            return this.mTransportManager.getCurrentTransportComponent();
        } catch (TransportNotRegisteredException e) {
            return null;
        } finally {
            Binder.restoreCallingIdentity(oldId);
        }
    }

    public String[] listAllTransports() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "listAllTransports");
        return this.mTransportManager.getRegisteredTransportNames();
    }

    public ComponentName[] listAllTransportComponents() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "listAllTransportComponents");
        return this.mTransportManager.getRegisteredTransportComponents();
    }

    public void updateTransportAttributes(ComponentName transportComponent, String name, Intent configurationIntent, String currentDestinationString, Intent dataManagementIntent, CharSequence dataManagementLabel) {
        updateTransportAttributes(Binder.getCallingUid(), transportComponent, name, configurationIntent, currentDestinationString, dataManagementIntent, dataManagementLabel);
    }

    /* Debug info: failed to restart local var, previous not found, register: 15 */
    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateTransportAttributes(int callingUid, ComponentName transportComponent, String name, Intent configurationIntent, String currentDestinationString, Intent dataManagementIntent, CharSequence dataManagementLabel) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "updateTransportAttributes");
        Preconditions.checkNotNull(transportComponent, "transportComponent can't be null");
        Preconditions.checkNotNull(name, "name can't be null");
        Preconditions.checkNotNull(currentDestinationString, "currentDestinationString can't be null");
        boolean z = true;
        if ((dataManagementIntent == null) != (dataManagementLabel == null)) {
            z = false;
        }
        Preconditions.checkArgument(z, "dataManagementLabel should be null iff dataManagementIntent is null");
        try {
            if (callingUid == this.mContext.getPackageManager().getPackageUidAsUser(transportComponent.getPackageName(), 0, this.mUserId)) {
                long oldId = Binder.clearCallingIdentity();
                try {
                    this.mTransportManager.updateTransportAttributes(transportComponent, name, configurationIntent, currentDestinationString, dataManagementIntent, dataManagementLabel);
                } finally {
                    Binder.restoreCallingIdentity(oldId);
                }
            } else {
                try {
                    throw new SecurityException("Only the transport can change its description");
                } catch (PackageManager.NameNotFoundException e) {
                    e = e;
                    throw new SecurityException("Transport package not found", e);
                }
            }
        } catch (PackageManager.NameNotFoundException e2) {
            e = e2;
            int i = callingUid;
            throw new SecurityException("Transport package not found", e);
        }
    }

    @Deprecated
    public String selectBackupTransport(String transportName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "selectBackupTransport");
        long oldId = Binder.clearCallingIdentity();
        try {
            String previousTransportName = this.mTransportManager.selectTransport(transportName);
            updateStateForTransport(transportName);
            Slog.v(BackupManagerService.TAG, "selectBackupTransport(transport = " + transportName + "): previous transport = " + previousTransportName);
            return previousTransportName;
        } finally {
            Binder.restoreCallingIdentity(oldId);
        }
    }

    public void selectBackupTransportAsync(ComponentName transportComponent, ISelectBackupTransportCallback listener) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "selectBackupTransportAsync");
        long oldId = Binder.clearCallingIdentity();
        try {
            String transportString = transportComponent.flattenToShortString();
            Slog.v(BackupManagerService.TAG, "selectBackupTransportAsync(transport = " + transportString + ")");
            this.mBackupHandler.post(new Runnable(transportComponent, listener) {
                private final /* synthetic */ ComponentName f$1;
                private final /* synthetic */ ISelectBackupTransportCallback f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    UserBackupManagerService.this.lambda$selectBackupTransportAsync$5$UserBackupManagerService(this.f$1, this.f$2);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(oldId);
        }
    }

    public /* synthetic */ void lambda$selectBackupTransportAsync$5$UserBackupManagerService(ComponentName transportComponent, ISelectBackupTransportCallback listener) {
        String transportName = null;
        int result = this.mTransportManager.registerAndSelectTransport(transportComponent);
        if (result == 0) {
            try {
                transportName = this.mTransportManager.getTransportName(transportComponent);
                updateStateForTransport(transportName);
            } catch (TransportNotRegisteredException e) {
                Slog.e(BackupManagerService.TAG, "Transport got unregistered");
                result = -1;
            }
        }
        if (transportName != null) {
            try {
                listener.onSuccess(transportName);
            } catch (RemoteException e2) {
                Slog.e(BackupManagerService.TAG, "ISelectBackupTransportCallback listener not available");
            }
        } else {
            listener.onFailure(result);
        }
    }

    private void updateStateForTransport(String newTransportName) {
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), "backup_transport", newTransportName, this.mUserId);
        TransportClient transportClient = this.mTransportManager.getTransportClient(newTransportName, "BMS.updateStateForTransport()");
        if (transportClient != null) {
            try {
                this.mCurrentToken = transportClient.connectOrThrow("BMS.updateStateForTransport()").getCurrentRestoreSet();
            } catch (Exception e) {
                this.mCurrentToken = 0;
                Slog.w(BackupManagerService.TAG, "Transport " + newTransportName + " not available: current token = 0");
            }
            this.mTransportManager.disposeOfTransportClient(transportClient, "BMS.updateStateForTransport()");
            return;
        }
        Slog.w(BackupManagerService.TAG, "Transport " + newTransportName + " not registered: current token = 0");
        this.mCurrentToken = 0;
    }

    public Intent getConfigurationIntent(String transportName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getConfigurationIntent");
        try {
            Intent intent = this.mTransportManager.getTransportConfigurationIntent(transportName);
            Slog.d(BackupManagerService.TAG, "getConfigurationIntent() returning intent " + intent);
            return intent;
        } catch (TransportNotRegisteredException e) {
            Slog.e(BackupManagerService.TAG, "Unable to get configuration intent from transport: " + e.getMessage());
            return null;
        }
    }

    public String getDestinationString(String transportName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getDestinationString");
        try {
            String string = this.mTransportManager.getTransportCurrentDestinationString(transportName);
            Slog.d(BackupManagerService.TAG, "getDestinationString() returning " + string);
            return string;
        } catch (TransportNotRegisteredException e) {
            Slog.e(BackupManagerService.TAG, "Unable to get destination string from transport: " + e.getMessage());
            return null;
        }
    }

    public Intent getDataManagementIntent(String transportName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getDataManagementIntent");
        try {
            Intent intent = this.mTransportManager.getTransportDataManagementIntent(transportName);
            Slog.d(BackupManagerService.TAG, "getDataManagementIntent() returning intent " + intent);
            return intent;
        } catch (TransportNotRegisteredException e) {
            Slog.e(BackupManagerService.TAG, "Unable to get management intent from transport: " + e.getMessage());
            return null;
        }
    }

    public CharSequence getDataManagementLabel(String transportName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "getDataManagementLabel");
        try {
            CharSequence label = this.mTransportManager.getTransportDataManagementLabel(transportName);
            Slog.d(BackupManagerService.TAG, "getDataManagementLabel() returning " + label);
            return label;
        } catch (TransportNotRegisteredException e) {
            Slog.e(BackupManagerService.TAG, "Unable to get management label from transport: " + e.getMessage());
            return null;
        }
    }

    public void agentConnected(String packageName, IBinder agentBinder) {
        synchronized (this.mAgentConnectLock) {
            if (Binder.getCallingUid() == 1000) {
                Slog.d(BackupManagerService.TAG, "agentConnected pkg=" + packageName + " agent=" + agentBinder);
                this.mConnectedAgent = IBackupAgent.Stub.asInterface(agentBinder);
                this.mConnecting = false;
            } else {
                Slog.w(BackupManagerService.TAG, "Non-system process uid=" + Binder.getCallingUid() + " claiming agent connected");
            }
            this.mAgentConnectLock.notifyAll();
        }
    }

    public void agentDisconnected(String packageName) {
        synchronized (this.mAgentConnectLock) {
            if (Binder.getCallingUid() == 1000) {
                this.mConnectedAgent = null;
                this.mConnecting = false;
            } else {
                Slog.w(BackupManagerService.TAG, "Non-system process uid=" + Binder.getCallingUid() + " claiming agent disconnected");
            }
            this.mAgentConnectLock.notifyAll();
        }
    }

    public void restoreAtInstall(String packageName, int token) {
        boolean skip;
        String str = packageName;
        if (Binder.getCallingUid() != 1000) {
            Slog.w(BackupManagerService.TAG, "Non-system process uid=" + Binder.getCallingUid() + " attemping install-time restore");
            return;
        }
        boolean skip2 = false;
        long restoreSet = getAvailableRestoreToken(packageName);
        Slog.v(BackupManagerService.TAG, "restoreAtInstall pkg=" + str + " token=" + Integer.toHexString(token) + " restoreSet=" + Long.toHexString(restoreSet));
        if (restoreSet == 0) {
            Slog.i(BackupManagerService.TAG, "No restore set");
            skip2 = true;
        }
        TransportClient transportClient = this.mTransportManager.getCurrentTransportClient("BMS.restoreAtInstall()");
        if (transportClient == null) {
            Slog.w(BackupManagerService.TAG, "No transport client");
            skip2 = true;
        }
        if (!this.mAutoRestore) {
            Slog.w(BackupManagerService.TAG, "Non-restorable state: auto=" + this.mAutoRestore);
            skip = true;
        } else {
            skip = skip2;
        }
        if (!skip) {
            try {
                this.mWakelock.acquire();
                OnTaskFinishedListener listener = new OnTaskFinishedListener(transportClient) {
                    private final /* synthetic */ TransportClient f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void onFinished(String str) {
                        UserBackupManagerService.this.lambda$restoreAtInstall$6$UserBackupManagerService(this.f$1, str);
                    }
                };
                Slog.d(BackupManagerService.TAG, "Restore at install of " + str);
                Message msg = this.mBackupHandler.obtainMessage(3);
                msg.obj = RestoreParams.createForRestoreAtInstall(transportClient, (IRestoreObserver) null, (IBackupManagerMonitor) null, restoreSet, packageName, token, listener);
                this.mBackupHandler.sendMessage(msg);
            } catch (Exception e) {
                Slog.e(BackupManagerService.TAG, "Unable to contact transport: " + e.getMessage());
                skip = true;
            }
        }
        if (skip) {
            if (transportClient != null) {
                this.mTransportManager.disposeOfTransportClient(transportClient, "BMS.restoreAtInstall()");
            }
            Slog.v(BackupManagerService.TAG, "Finishing install immediately");
            try {
                try {
                    this.mPackageManagerBinder.finishPackageInstall(token, false);
                } catch (RemoteException e2) {
                }
            } catch (RemoteException e3) {
                int i = token;
            }
        } else {
            int i2 = token;
        }
    }

    public /* synthetic */ void lambda$restoreAtInstall$6$UserBackupManagerService(TransportClient transportClient, String caller) {
        this.mTransportManager.disposeOfTransportClient(transportClient, caller);
        this.mWakelock.release();
    }

    public IRestoreSession beginRestoreSession(String packageName, String transport) {
        Slog.v(BackupManagerService.TAG, "beginRestoreSession: pkg=" + packageName + " transport=" + transport);
        boolean needPermission = true;
        if (transport == null) {
            transport = this.mTransportManager.getCurrentTransportName();
            if (packageName != null) {
                try {
                    if (this.mPackageManager.getPackageInfoAsUser(packageName, 0, this.mUserId).applicationInfo.uid == Binder.getCallingUid()) {
                        needPermission = false;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.w(BackupManagerService.TAG, "Asked to restore nonexistent pkg " + packageName);
                    throw new IllegalArgumentException("Package " + packageName + " not found");
                }
            }
        }
        if (needPermission) {
            this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "beginRestoreSession");
        } else {
            Slog.d(BackupManagerService.TAG, "restoring self on current transport; no permission needed");
        }
        synchronized (this) {
            if (this.mActiveRestoreSession != null) {
                Slog.i(BackupManagerService.TAG, "Restore session requested but one already active");
                return null;
            } else if (this.mBackupRunning) {
                Slog.i(BackupManagerService.TAG, "Restore session requested but currently running backups");
                return null;
            } else {
                this.mActiveRestoreSession = new ActiveRestoreSession(this, packageName, transport);
                this.mBackupHandler.sendEmptyMessageDelayed(8, this.mAgentTimeoutParameters.getRestoreAgentTimeoutMillis());
                return this.mActiveRestoreSession;
            }
        }
    }

    public void clearRestoreSession(ActiveRestoreSession currentSession) {
        synchronized (this) {
            if (currentSession != this.mActiveRestoreSession) {
                Slog.e(BackupManagerService.TAG, "ending non-current restore session");
            } else {
                Slog.v(BackupManagerService.TAG, "Clearing restore session and halting timeout");
                this.mActiveRestoreSession = null;
                this.mBackupHandler.removeMessages(8);
            }
        }
    }

    public void opComplete(int token, long result) {
        Operation op;
        Slog.v(BackupManagerService.TAG, "opComplete: " + Integer.toHexString(token) + " result=" + result);
        synchronized (this.mCurrentOpLock) {
            op = this.mCurrentOperations.get(token);
            if (op != null) {
                if (op.state == -1) {
                    op = null;
                    this.mCurrentOperations.delete(token);
                } else if (op.state == 1) {
                    Slog.w(BackupManagerService.TAG, "Received duplicate ack for token=" + Integer.toHexString(token));
                    op = null;
                    this.mCurrentOperations.remove(token);
                } else if (op.state == 0) {
                    op.state = 1;
                }
            }
            this.mCurrentOpLock.notifyAll();
        }
        if (op != null && op.callback != null) {
            this.mBackupHandler.sendMessage(this.mBackupHandler.obtainMessage(21, Pair.create(op.callback, Long.valueOf(result))));
        }
    }

    public boolean isAppEligibleForBackup(String packageName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "isAppEligibleForBackup");
        long oldToken = Binder.clearCallingIdentity();
        try {
            TransportClient transportClient = this.mTransportManager.getCurrentTransportClient("BMS.isAppEligibleForBackup");
            boolean eligible = AppBackupUtils.appIsRunningAndEligibleForBackupWithTransport(transportClient, packageName, this.mPackageManager, this.mUserId);
            if (transportClient != null) {
                this.mTransportManager.disposeOfTransportClient(transportClient, "BMS.isAppEligibleForBackup");
            }
            return eligible;
        } finally {
            Binder.restoreCallingIdentity(oldToken);
        }
    }

    public String[] filterAppsEligibleForBackup(String[] packages) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.BACKUP", "filterAppsEligibleForBackup");
        long oldToken = Binder.clearCallingIdentity();
        try {
            TransportClient transportClient = this.mTransportManager.getCurrentTransportClient("BMS.filterAppsEligibleForBackup");
            List<String> eligibleApps = new LinkedList<>();
            for (String packageName : packages) {
                if (AppBackupUtils.appIsRunningAndEligibleForBackupWithTransport(transportClient, packageName, this.mPackageManager, this.mUserId)) {
                    eligibleApps.add(packageName);
                }
            }
            if (transportClient != null) {
                this.mTransportManager.disposeOfTransportClient(transportClient, "BMS.filterAppsEligibleForBackup");
            }
            return (String[]) eligibleApps.toArray(new String[eligibleApps.size()]);
        } finally {
            Binder.restoreCallingIdentity(oldToken);
        }
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        long identityToken = Binder.clearCallingIdentity();
        if (args != null) {
            try {
                int length = args.length;
                int i = 0;
                while (i < length) {
                    String arg = args[i];
                    if ("-h".equals(arg)) {
                        pw.println("'dumpsys backup' optional arguments:");
                        pw.println("  -h       : this help text");
                        pw.println("  a[gents] : dump information about defined backup agents");
                        pw.println("  users    : dump the list of users for which backup service is running");
                        Binder.restoreCallingIdentity(identityToken);
                        return;
                    } else if ("agents".startsWith(arg)) {
                        dumpAgents(pw);
                        Binder.restoreCallingIdentity(identityToken);
                        return;
                    } else if ("transportclients".equals(arg.toLowerCase())) {
                        this.mTransportManager.dumpTransportClients(pw);
                        Binder.restoreCallingIdentity(identityToken);
                        return;
                    } else if ("transportstats".equals(arg.toLowerCase())) {
                        this.mTransportManager.dumpTransportStats(pw);
                        return;
                    } else {
                        i++;
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(identityToken);
            }
        }
        dumpInternal(pw);
        Binder.restoreCallingIdentity(identityToken);
    }

    private void dumpAgents(PrintWriter pw) {
        List<PackageInfo> agentPackages = allAgentPackages();
        pw.println("Defined backup agents:");
        for (PackageInfo pkg : agentPackages) {
            pw.print("  ");
            pw.print(pkg.packageName);
            pw.println(':');
            pw.print("      ");
            pw.println(pkg.applicationInfo.backupAgentName);
        }
    }

    private void dumpInternal(PrintWriter pw) {
        String str;
        synchronized (this.mQueueLock) {
            StringBuilder sb = new StringBuilder();
            sb.append("Backup Manager is ");
            sb.append(this.mEnabled ? "enabled" : "disabled");
            sb.append(" / ");
            sb.append(!this.mSetupComplete ? "not " : "");
            sb.append("setup complete / ");
            sb.append(this.mPendingInits.size() == 0 ? "not " : "");
            sb.append("pending init");
            pw.println(sb.toString());
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Auto-restore is ");
            sb2.append(this.mAutoRestore ? "enabled" : "disabled");
            pw.println(sb2.toString());
            if (this.mBackupRunning) {
                pw.println("Backup currently running");
            }
            pw.println(isBackupOperationInProgress() ? "Backup in progress" : "No backups running");
            pw.println("Last backup pass started: " + this.mLastBackupPass + " (now = " + System.currentTimeMillis() + ')');
            StringBuilder sb3 = new StringBuilder();
            sb3.append("  next scheduled: ");
            sb3.append(KeyValueBackupJob.nextScheduled(this.mUserId));
            pw.println(sb3.toString());
            pw.println("Transport whitelist:");
            for (ComponentName transport : this.mTransportManager.getTransportWhitelist()) {
                pw.print("    ");
                pw.println(transport.flattenToShortString());
            }
            pw.println("Available transports:");
            String[] transports = listAllTransports();
            if (transports != null) {
                for (String t : transports) {
                    StringBuilder sb4 = new StringBuilder();
                    if (t.equals(this.mTransportManager.getCurrentTransportName())) {
                        str = "  * ";
                    } else {
                        str = "    ";
                    }
                    sb4.append(str);
                    sb4.append(t);
                    pw.println(sb4.toString());
                    try {
                        File dir = new File(this.mBaseStateDir, this.mTransportManager.getTransportDirName(t));
                        pw.println("       destination: " + this.mTransportManager.getTransportCurrentDestinationString(t));
                        pw.println("       intent: " + this.mTransportManager.getTransportConfigurationIntent(t));
                        File[] listFiles = dir.listFiles();
                        int length = listFiles.length;
                        for (int i = 0; i < length; i++) {
                            File f = listFiles[i];
                            pw.println("       " + f.getName() + " - " + f.length() + " state bytes");
                        }
                    } catch (Exception e) {
                        Slog.e(BackupManagerService.TAG, "Error in transport", e);
                        pw.println("        Error: " + e);
                    }
                }
            }
            this.mTransportManager.dumpTransportClients(pw);
            pw.println("Pending init: " + this.mPendingInits.size());
            Iterator<String> it = this.mPendingInits.iterator();
            while (it.hasNext()) {
                pw.println("    " + it.next());
            }
            pw.print("Ancestral: ");
            pw.println(Long.toHexString(this.mAncestralToken));
            pw.print("Current:   ");
            pw.println(Long.toHexString(this.mCurrentToken));
            int numPackages = this.mBackupParticipants.size();
            pw.println("Participants:");
            for (int i2 = 0; i2 < numPackages; i2++) {
                int uid = this.mBackupParticipants.keyAt(i2);
                pw.print("  uid: ");
                pw.println(uid);
                Iterator<String> it2 = this.mBackupParticipants.valueAt(i2).iterator();
                while (it2.hasNext()) {
                    pw.println("    " + it2.next());
                }
            }
            StringBuilder sb5 = new StringBuilder();
            sb5.append("Ancestral packages: ");
            sb5.append(this.mAncestralPackages == null ? "none" : Integer.valueOf(this.mAncestralPackages.size()));
            pw.println(sb5.toString());
            if (this.mAncestralPackages != null) {
                for (String pkg : this.mAncestralPackages) {
                    pw.println("    " + pkg);
                }
            }
            Set<String> processedPackages = this.mProcessedPackagesJournal.getPackagesCopy();
            pw.println("Ever backed up: " + processedPackages.size());
            for (String pkg2 : processedPackages) {
                pw.println("    " + pkg2);
            }
            pw.println("Pending key/value backup: " + this.mPendingBackups.size());
            for (BackupRequest req : this.mPendingBackups.values()) {
                pw.println("    " + req);
            }
            pw.println("Full backup queue:" + this.mFullBackupQueue.size());
            Iterator<FullBackupEntry> it3 = this.mFullBackupQueue.iterator();
            while (it3.hasNext()) {
                FullBackupEntry entry = it3.next();
                pw.print("    ");
                pw.print(entry.lastBackup);
                pw.print(" : ");
                pw.println(entry.packageName);
            }
        }
    }

    public IBackupManager getBackupManagerBinder() {
        return this.mBackupManagerBinder;
    }

    /* access modifiers changed from: package-private */
    public void cancelMiuiBackups() {
        Slog.i(BackupManagerService.TAG, "cancelMiuiBackups() called. mCallerFd=" + this.mCallerFd);
        if (BackupManagerServiceInjector.isRunningFromMiui(this.mCallerFd)) {
            BackupManagerServiceInjector.cancelBackups(this);
        }
    }

    public int getAppUserId() {
        return BackupManagerServiceInjector.getAppUserId(this.mCallerFd, this.mUserId);
    }
}
