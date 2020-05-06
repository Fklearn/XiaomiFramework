package com.android.server.appop;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.AppOpsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManagerInternal;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.os.storage.StorageManagerInternal;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.KeyValueListParser;
import android.util.LongSparseArray;
import android.util.LongSparseLongArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsActiveCallback;
import com.android.internal.app.IAppOpsCallback;
import com.android.internal.app.IAppOpsNotedCallback;
import com.android.internal.app.IAppOpsService;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.internal.util.function.QuadFunction;
import com.android.internal.util.function.TriFunction;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.AppOpsServiceState;
import com.android.server.LocalServices;
import com.android.server.LockGuard;
import com.android.server.SystemService;
import com.android.server.appop.AppOpsService;
import com.android.server.display.color.DisplayTransformManager;
import com.android.server.job.controllers.JobStatus;
import com.android.server.net.watchlist.WatchlistLoggingHandler;
import com.android.server.notification.NotificationShellCmd;
import com.android.server.pm.DefaultPermissionGrantPolicyInjector;
import com.android.server.pm.PackageManagerService;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.wm.ActivityTaskManagerService;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import libcore.util.EmptyArray;
import miui.os.Build;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class AppOpsService extends IAppOpsService.Stub {
    private static final int CURRENT_VERSION = 1;
    static final boolean DEBUG = false;
    private static final int NO_VERSION = -1;
    /* access modifiers changed from: private */
    public static final int[] OPS_RESTRICTED_ON_SUSPEND = {28, 27, 26};
    private static final int[] PROCESS_STATE_TO_UID_STATE = {100, 100, 200, DisplayTransformManager.LEVEL_COLOR_MATRIX_INVERT_COLOR, 500, 400, 500, 500, SystemService.PHASE_THIRD_PARTY_APPS_CAN_START, SystemService.PHASE_THIRD_PARTY_APPS_CAN_START, SystemService.PHASE_THIRD_PARTY_APPS_CAN_START, SystemService.PHASE_THIRD_PARTY_APPS_CAN_START, SystemService.PHASE_THIRD_PARTY_APPS_CAN_START, 700, 700, 700, 700, 700, 700, 700, 700, 700};
    static final String TAG = "AppOps";
    private static final int UID_ANY = -2;
    static final long WRITE_DELAY = 1800000;
    final ArrayMap<IBinder, SparseArray<ActiveCallback>> mActiveWatchers = new ArrayMap<>();
    private final AppOpsManagerInternalImpl mAppOpsManagerInternal = new AppOpsManagerInternalImpl();
    final SparseArray<SparseArray<Restriction>> mAudioRestrictions = new SparseArray<>();
    @GuardedBy({"this"})
    private AppOpsManagerInternal.CheckOpsDelegate mCheckOpsDelegate;
    final ArrayMap<IBinder, ClientState> mClients = new ArrayMap<>();
    @VisibleForTesting
    final Constants mConstants;
    Context mContext;
    private List<String> mDefaultGrantList;
    boolean mFastWriteScheduled;
    final AtomicFile mFile;
    final Handler mHandler;
    @VisibleForTesting
    final HistoricalRegistry mHistoricalRegistry = new HistoricalRegistry(this);
    long mLastRealtime;
    final ArrayMap<IBinder, ModeCallback> mModeWatchers = new ArrayMap<>();
    final ArrayMap<IBinder, SparseArray<NotedCallback>> mNotedWatchers = new ArrayMap<>();
    final SparseArray<ArraySet<ModeCallback>> mOpModeWatchers = new SparseArray<>();
    /* access modifiers changed from: private */
    public final ArrayMap<IBinder, ClientRestrictionState> mOpUserRestrictions = new ArrayMap<>();
    final ArrayMap<String, ArraySet<ModeCallback>> mPackageModeWatchers = new ArrayMap<>();
    SparseIntArray mProfileOwners;
    private AppOpsServiceState mServiceState;
    @GuardedBy({"this"})
    @VisibleForTesting
    final SparseArray<UidState> mUidStates = new SparseArray<>();
    final Runnable mWriteRunner = new Runnable() {
        public void run() {
            synchronized (AppOpsService.this) {
                AppOpsService.this.mWriteScheduled = false;
                AppOpsService.this.mFastWriteScheduled = false;
                new AsyncTask<Void, Void, Void>() {
                    /* access modifiers changed from: protected */
                    public Void doInBackground(Void... params) {
                        AppOpsService.this.writeState();
                        return null;
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
            }
        }
    };
    boolean mWriteScheduled;

    @VisibleForTesting
    final class Constants extends ContentObserver {
        private static final String KEY_BG_STATE_SETTLE_TIME = "bg_state_settle_time";
        private static final String KEY_FG_SERVICE_STATE_SETTLE_TIME = "fg_service_state_settle_time";
        private static final String KEY_TOP_STATE_SETTLE_TIME = "top_state_settle_time";
        public long BG_STATE_SETTLE_TIME;
        public long FG_SERVICE_STATE_SETTLE_TIME;
        public long TOP_STATE_SETTLE_TIME;
        private final KeyValueListParser mParser = new KeyValueListParser(',');
        private ContentResolver mResolver;

        public Constants(Handler handler) {
            super(handler);
            updateConstants();
        }

        public void startMonitoring(ContentResolver resolver) {
            this.mResolver = resolver;
            this.mResolver.registerContentObserver(Settings.Global.getUriFor("app_ops_constants"), false, this);
            updateConstants();
        }

        public void onChange(boolean selfChange, Uri uri) {
            updateConstants();
        }

        private void updateConstants() {
            String value;
            ContentResolver contentResolver = this.mResolver;
            if (contentResolver != null) {
                value = Settings.Global.getString(contentResolver, "app_ops_constants");
            } else {
                value = "";
            }
            synchronized (AppOpsService.this) {
                try {
                    this.mParser.setString(value);
                } catch (IllegalArgumentException e) {
                    Slog.e(AppOpsService.TAG, "Bad app ops settings", e);
                }
                this.TOP_STATE_SETTLE_TIME = this.mParser.getDurationMillis(KEY_TOP_STATE_SETTLE_TIME, 30000);
                this.FG_SERVICE_STATE_SETTLE_TIME = this.mParser.getDurationMillis(KEY_FG_SERVICE_STATE_SETTLE_TIME, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
                this.BG_STATE_SETTLE_TIME = this.mParser.getDurationMillis(KEY_BG_STATE_SETTLE_TIME, 1000);
            }
        }

        /* access modifiers changed from: package-private */
        public void dump(PrintWriter pw) {
            pw.println("  Settings:");
            pw.print("    ");
            pw.print(KEY_TOP_STATE_SETTLE_TIME);
            pw.print("=");
            TimeUtils.formatDuration(this.TOP_STATE_SETTLE_TIME, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_FG_SERVICE_STATE_SETTLE_TIME);
            pw.print("=");
            TimeUtils.formatDuration(this.FG_SERVICE_STATE_SETTLE_TIME, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_BG_STATE_SETTLE_TIME);
            pw.print("=");
            TimeUtils.formatDuration(this.BG_STATE_SETTLE_TIME, pw);
            pw.println();
        }
    }

    @VisibleForTesting
    static final class UidState {
        public SparseBooleanArray foregroundOps;
        public boolean hasForegroundWatchers;
        public SparseIntArray opModes;
        public int pendingState = 700;
        public long pendingStateCommitTime;
        public ArrayMap<String, Ops> pkgOps;
        public int startNesting;
        public int state = 700;
        public final int uid;

        public UidState(int uid2) {
            this.uid = uid2;
        }

        public void clear() {
            this.pkgOps = null;
            this.opModes = null;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
            r0 = r2.opModes;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isDefault() {
            /*
                r2 = this;
                android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r0 = r2.pkgOps
                if (r0 == 0) goto L_0x000a
                boolean r0 = r0.isEmpty()
                if (r0 == 0) goto L_0x0020
            L_0x000a:
                android.util.SparseIntArray r0 = r2.opModes
                if (r0 == 0) goto L_0x0014
                int r0 = r0.size()
                if (r0 > 0) goto L_0x0020
            L_0x0014:
                int r0 = r2.state
                r1 = 700(0x2bc, float:9.81E-43)
                if (r0 != r1) goto L_0x0020
                int r0 = r2.pendingState
                if (r0 != r1) goto L_0x0020
                r0 = 1
                goto L_0x0021
            L_0x0020:
                r0 = 0
            L_0x0021:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.UidState.isDefault():boolean");
        }

        /* access modifiers changed from: package-private */
        public int evalMode(int op, int mode) {
            if (mode == 4) {
                return this.state <= AppOpsManager.resolveFirstUnrestrictedUidState(op) ? 0 : 1;
            }
            return mode;
        }

        private void evalForegroundWatchers(int op, SparseArray<ArraySet<ModeCallback>> watchers, SparseBooleanArray which) {
            boolean curValue = which.get(op, false);
            ArraySet<ModeCallback> callbacks = watchers.get(op);
            if (callbacks != null) {
                int cbi = callbacks.size() - 1;
                while (!curValue && cbi >= 0) {
                    if ((callbacks.valueAt(cbi).mFlags & 1) != 0) {
                        this.hasForegroundWatchers = true;
                        curValue = true;
                    }
                    cbi--;
                }
            }
            which.put(op, curValue);
        }

        public void evalForegroundOps(SparseArray<ArraySet<ModeCallback>> watchers) {
            SparseBooleanArray which = null;
            this.hasForegroundWatchers = false;
            SparseIntArray sparseIntArray = this.opModes;
            if (sparseIntArray != null) {
                for (int i = sparseIntArray.size() - 1; i >= 0; i--) {
                    if (this.opModes.valueAt(i) == 4) {
                        if (which == null) {
                            which = new SparseBooleanArray();
                        }
                        evalForegroundWatchers(this.opModes.keyAt(i), watchers, which);
                    }
                }
            }
            ArrayMap<String, Ops> arrayMap = this.pkgOps;
            if (arrayMap != null) {
                for (int i2 = arrayMap.size() - 1; i2 >= 0; i2--) {
                    Ops ops = this.pkgOps.valueAt(i2);
                    for (int j = ops.size() - 1; j >= 0; j--) {
                        if (((Op) ops.valueAt(j)).mode == 4) {
                            if (which == null) {
                                which = new SparseBooleanArray();
                            }
                            evalForegroundWatchers(ops.keyAt(j), watchers, which);
                        }
                    }
                }
            }
            this.foregroundOps = which;
        }
    }

    static final class Ops extends SparseArray<Op> {
        final boolean isPrivileged;
        final String packageName;
        final UidState uidState;

        Ops(String _packageName, UidState _uidState, boolean _isPrivileged) {
            this.packageName = _packageName;
            this.uidState = _uidState;
            this.isPrivileged = _isPrivileged;
        }
    }

    static final class Op {
        /* access modifiers changed from: private */
        public LongSparseLongArray mAccessTimes;
        /* access modifiers changed from: private */
        public LongSparseLongArray mDurations;
        /* access modifiers changed from: private */
        public LongSparseArray<String> mProxyPackageNames;
        /* access modifiers changed from: private */
        public LongSparseLongArray mProxyUids;
        /* access modifiers changed from: private */
        public LongSparseLongArray mRejectTimes;
        /* access modifiers changed from: private */
        public int mode;
        int op;
        final String packageName;
        boolean running;
        int startNesting;
        long startRealtime;
        final UidState uidState;

        Op(UidState uidState2, String packageName2, int op2) {
            this.op = op2;
            this.uidState = uidState2;
            this.packageName = packageName2;
            this.mode = AppOpsManager.opToDefaultMode(op2);
        }

        /* access modifiers changed from: package-private */
        public int getMode() {
            return this.mode;
        }

        /* access modifiers changed from: package-private */
        public int evalMode() {
            return this.uidState.evalMode(this.op, this.mode);
        }

        public void accessed(long time, int proxyUid, String proxyPackageName, int uidState2, int flags) {
            long key = AppOpsManager.makeKey(uidState2, flags);
            if (this.mAccessTimes == null) {
                this.mAccessTimes = new LongSparseLongArray();
            }
            this.mAccessTimes.put(key, time);
            updateProxyState(key, proxyUid, proxyPackageName);
            LongSparseLongArray longSparseLongArray = this.mDurations;
            if (longSparseLongArray != null) {
                longSparseLongArray.delete(key);
            }
        }

        public void rejected(long time, int proxyUid, String proxyPackageName, int uidState2, int flags) {
            long key = AppOpsManager.makeKey(uidState2, flags);
            if (this.mRejectTimes == null) {
                this.mRejectTimes = new LongSparseLongArray();
            }
            this.mRejectTimes.put(key, time);
            updateProxyState(key, proxyUid, proxyPackageName);
            LongSparseLongArray longSparseLongArray = this.mDurations;
            if (longSparseLongArray != null) {
                longSparseLongArray.delete(key);
            }
        }

        public void started(long time, int uidState2, int flags) {
            updateAccessTimeAndDuration(time, -1, uidState2, flags);
            this.running = true;
        }

        public void finished(long time, long duration, int uidState2, int flags) {
            updateAccessTimeAndDuration(time, duration, uidState2, flags);
            this.running = false;
        }

        public void running(long time, long duration, int uidState2, int flags) {
            updateAccessTimeAndDuration(time, duration, uidState2, flags);
        }

        public void continuing(long duration, int uidState2, int flags) {
            long key = AppOpsManager.makeKey(uidState2, flags);
            if (this.mDurations == null) {
                this.mDurations = new LongSparseLongArray();
            }
            this.mDurations.put(key, duration);
        }

        private void updateAccessTimeAndDuration(long time, long duration, int uidState2, int flags) {
            long key = AppOpsManager.makeKey(uidState2, flags);
            if (this.mAccessTimes == null) {
                this.mAccessTimes = new LongSparseLongArray();
            }
            this.mAccessTimes.put(key, time);
            if (this.mDurations == null) {
                this.mDurations = new LongSparseLongArray();
            }
            this.mDurations.put(key, duration);
        }

        private void updateProxyState(long key, int proxyUid, String proxyPackageName) {
            if (this.mProxyUids == null) {
                this.mProxyUids = new LongSparseLongArray();
            }
            this.mProxyUids.put(key, (long) proxyUid);
            if (this.mProxyPackageNames == null) {
                this.mProxyPackageNames = new LongSparseArray<>();
            }
            this.mProxyPackageNames.put(key, proxyPackageName);
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
            r0 = r1.mRejectTimes;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean hasAnyTime() {
            /*
                r1 = this;
                android.util.LongSparseLongArray r0 = r1.mAccessTimes
                if (r0 == 0) goto L_0x000a
                int r0 = r0.size()
                if (r0 > 0) goto L_0x0014
            L_0x000a:
                android.util.LongSparseLongArray r0 = r1.mRejectTimes
                if (r0 == 0) goto L_0x0016
                int r0 = r0.size()
                if (r0 <= 0) goto L_0x0016
            L_0x0014:
                r0 = 1
                goto L_0x0017
            L_0x0016:
                r0 = 0
            L_0x0017:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.Op.hasAnyTime():boolean");
        }
    }

    final class ModeCallback implements IBinder.DeathRecipient {
        final IAppOpsCallback mCallback;
        final int mCallingPid;
        final int mCallingUid;
        final int mFlags;
        final int mWatchingUid;

        ModeCallback(IAppOpsCallback callback, int watchingUid, int flags, int callingUid, int callingPid) {
            this.mCallback = callback;
            this.mWatchingUid = watchingUid;
            this.mFlags = flags;
            this.mCallingUid = callingUid;
            this.mCallingPid = callingPid;
            try {
                this.mCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:2:0x0003, code lost:
            r0 = r1.mWatchingUid;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean isWatchingUid(int r2) {
            /*
                r1 = this;
                r0 = -2
                if (r2 == r0) goto L_0x000c
                int r0 = r1.mWatchingUid
                if (r0 < 0) goto L_0x000c
                if (r0 != r2) goto L_0x000a
                goto L_0x000c
            L_0x000a:
                r0 = 0
                goto L_0x000d
            L_0x000c:
                r0 = 1
            L_0x000d:
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.ModeCallback.isWatchingUid(int):boolean");
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ModeCallback{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" watchinguid=");
            UserHandle.formatUid(sb, this.mWatchingUid);
            sb.append(" flags=0x");
            sb.append(Integer.toHexString(this.mFlags));
            sb.append(" from uid=");
            UserHandle.formatUid(sb, this.mCallingUid);
            sb.append(" pid=");
            sb.append(this.mCallingPid);
            sb.append('}');
            return sb.toString();
        }

        /* access modifiers changed from: package-private */
        public void unlinkToDeath() {
            this.mCallback.asBinder().unlinkToDeath(this, 0);
        }

        public void binderDied() {
            AppOpsService.this.stopWatchingMode(this.mCallback);
        }
    }

    final class ActiveCallback implements IBinder.DeathRecipient {
        final IAppOpsActiveCallback mCallback;
        final int mCallingPid;
        final int mCallingUid;
        final int mWatchingUid;

        ActiveCallback(IAppOpsActiveCallback callback, int watchingUid, int callingUid, int callingPid) {
            this.mCallback = callback;
            this.mWatchingUid = watchingUid;
            this.mCallingUid = callingUid;
            this.mCallingPid = callingPid;
            try {
                this.mCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("ActiveCallback{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" watchinguid=");
            UserHandle.formatUid(sb, this.mWatchingUid);
            sb.append(" from uid=");
            UserHandle.formatUid(sb, this.mCallingUid);
            sb.append(" pid=");
            sb.append(this.mCallingPid);
            sb.append('}');
            return sb.toString();
        }

        /* access modifiers changed from: package-private */
        public void destroy() {
            this.mCallback.asBinder().unlinkToDeath(this, 0);
        }

        public void binderDied() {
            AppOpsService.this.stopWatchingActive(this.mCallback);
        }
    }

    final class NotedCallback implements IBinder.DeathRecipient {
        final IAppOpsNotedCallback mCallback;
        final int mCallingPid;
        final int mCallingUid;
        final int mWatchingUid;

        NotedCallback(IAppOpsNotedCallback callback, int watchingUid, int callingUid, int callingPid) {
            this.mCallback = callback;
            this.mWatchingUid = watchingUid;
            this.mCallingUid = callingUid;
            this.mCallingPid = callingPid;
            try {
                this.mCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
            }
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            sb.append("NotedCallback{");
            sb.append(Integer.toHexString(System.identityHashCode(this)));
            sb.append(" watchinguid=");
            UserHandle.formatUid(sb, this.mWatchingUid);
            sb.append(" from uid=");
            UserHandle.formatUid(sb, this.mCallingUid);
            sb.append(" pid=");
            sb.append(this.mCallingPid);
            sb.append('}');
            return sb.toString();
        }

        /* access modifiers changed from: package-private */
        public void destroy() {
            this.mCallback.asBinder().unlinkToDeath(this, 0);
        }

        public void binderDied() {
            AppOpsService.this.stopWatchingNoted(this.mCallback);
        }
    }

    final class ClientState extends Binder implements IBinder.DeathRecipient {
        final IBinder mAppToken;
        final int mPid;
        final ArrayList<Op> mStartedOps = new ArrayList<>();

        ClientState(IBinder appToken) {
            this.mAppToken = appToken;
            this.mPid = Binder.getCallingPid();
            if (!(appToken instanceof Binder)) {
                try {
                    this.mAppToken.linkToDeath(this, 0);
                } catch (RemoteException e) {
                }
            }
        }

        public String toString() {
            return "ClientState{mAppToken=" + this.mAppToken + ", pid=" + this.mPid + '}';
        }

        public void binderDied() {
            synchronized (AppOpsService.this) {
                for (int i = this.mStartedOps.size() - 1; i >= 0; i--) {
                    AppOpsService.this.finishOperationLocked(this.mStartedOps.get(i), true);
                }
                AppOpsService.this.mClients.remove(this.mAppToken);
            }
        }
    }

    public AppOpsService(File storagePath, Handler handler) {
        LockGuard.installLock((Object) this, 0);
        this.mFile = new AtomicFile(storagePath, "appops");
        this.mHandler = handler;
        this.mConstants = new Constants(this.mHandler);
        readState();
        this.mServiceState = new AppOpsServiceState();
        this.mDefaultGrantList = Arrays.asList(DefaultPermissionGrantPolicyInjector.MIUI_APPS_GLOBAL);
    }

    public void publish(Context context) {
        this.mContext = context;
        ServiceManager.addService("appops", asBinder());
        LocalServices.addService(AppOpsManagerInternal.class, this.mAppOpsManagerInternal);
        this.mServiceState.init(this.mContext);
    }

    public void systemReady() {
        this.mServiceState.systemReady();
        this.mConstants.startMonitoring(this.mContext.getContentResolver());
        this.mHistoricalRegistry.systemReady(this.mContext.getContentResolver());
        synchronized (this) {
            boolean changed = false;
            for (int i = this.mUidStates.size() - 1; i >= 0; i--) {
                UidState uidState = this.mUidStates.valueAt(i);
                if (ArrayUtils.isEmpty(getPackagesForUid(uidState.uid))) {
                    uidState.clear();
                    this.mUidStates.removeAt(i);
                    changed = true;
                } else {
                    ArrayMap<String, Ops> pkgs = uidState.pkgOps;
                    if (pkgs != null) {
                        Iterator<Ops> it = pkgs.values().iterator();
                        while (it.hasNext()) {
                            Ops ops = it.next();
                            int curUid = -1;
                            try {
                                curUid = AppGlobals.getPackageManager().getPackageUid(ops.packageName, 8192, UserHandle.getUserId(ops.uidState.uid));
                            } catch (RemoteException e) {
                            }
                            if (curUid != ops.uidState.uid) {
                                Slog.i(TAG, "Pruning old package " + ops.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + ops.uidState + ": new uid=" + curUid);
                                it.remove();
                                changed = true;
                            }
                        }
                        if (uidState.isDefault()) {
                            this.mUidStates.removeAt(i);
                        }
                    }
                }
            }
            if (changed) {
                scheduleFastWriteLocked();
            }
        }
        IntentFilter packageSuspendFilter = new IntentFilter();
        packageSuspendFilter.addAction("android.intent.action.PACKAGES_UNSUSPENDED");
        packageSuspendFilter.addAction("android.intent.action.PACKAGES_SUSPENDED");
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int[] changedUids = intent.getIntArrayExtra("android.intent.extra.changed_uid_list");
                String[] changedPkgs = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                for (int code : AppOpsService.OPS_RESTRICTED_ON_SUSPEND) {
                    synchronized (AppOpsService.this) {
                        ArraySet<ModeCallback> callbacks = AppOpsService.this.mOpModeWatchers.get(code);
                        if (callbacks != null) {
                            ArraySet arraySet = new ArraySet(callbacks);
                            for (int i = 0; i < changedUids.length; i++) {
                                AppOpsService.this.notifyOpChanged((ArraySet<ModeCallback>) arraySet, code, changedUids[i], changedPkgs[i]);
                            }
                        }
                    }
                }
            }
        }, packageSuspendFilter);
        ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).setExternalSourcesPolicy(new PackageManagerInternal.ExternalSourcesPolicy() {
            public int getPackageTrustedToInstallApps(String packageName, int uid) {
                int appOpMode = AppOpsService.this.checkOperation(66, uid, packageName);
                if (appOpMode == 0) {
                    return 0;
                }
                if (appOpMode != 2) {
                    return 2;
                }
                return 1;
            }
        });
        if (!StorageManager.hasIsolatedStorage()) {
            ((StorageManagerInternal) LocalServices.getService(StorageManagerInternal.class)).addExternalStoragePolicy(new StorageManagerInternal.ExternalStorageMountPolicy() {
                public int getMountMode(int uid, String packageName) {
                    if (Process.isIsolated(uid)) {
                        return 0;
                    }
                    int readMode = AppOpsService.this.checkOperationRaw(59, uid, packageName);
                    if (readMode != 0 && readMode != 4) {
                        return 0;
                    }
                    int writeMode = AppOpsService.this.checkOperationRaw(60, uid, packageName);
                    if (writeMode == 0 || writeMode == 4) {
                        return 3;
                    }
                    return 2;
                }

                public boolean hasExternalStorage(int uid, String packageName) {
                    int mountMode = getMountMode(uid, packageName);
                    return mountMode == 2 || mountMode == 3;
                }
            });
        }
    }

    public void packageRemoved(int uid, String packageName) {
        synchronized (this) {
            UidState uidState = this.mUidStates.get(uid);
            if (uidState != null) {
                Ops ops = null;
                if (uidState.pkgOps != null) {
                    ops = uidState.pkgOps.remove(packageName);
                }
                if (ops != null && uidState.pkgOps.isEmpty() && getPackagesForUid(uid).length <= 0) {
                    this.mUidStates.remove(uid);
                }
                int clientCount = this.mClients.size();
                for (int i = 0; i < clientCount; i++) {
                    ClientState client = this.mClients.valueAt(i);
                    if (client.mStartedOps != null) {
                        for (int j = client.mStartedOps.size() - 1; j >= 0; j--) {
                            Op op = client.mStartedOps.get(j);
                            if (uid == op.uidState.uid && packageName.equals(op.packageName)) {
                                finishOperationLocked(op, true);
                                client.mStartedOps.remove(j);
                                if (op.startNesting <= 0) {
                                    scheduleOpActiveChangedIfNeededLocked(op.op, uid, packageName, false);
                                }
                            }
                        }
                    }
                }
                if (ops != null) {
                    scheduleFastWriteLocked();
                    int opCount = ops.size();
                    for (int i2 = 0; i2 < opCount; i2++) {
                        Op op2 = (Op) ops.valueAt(i2);
                        if (op2.running) {
                            scheduleOpActiveChangedIfNeededLocked(op2.op, op2.uidState.uid, op2.packageName, false);
                        }
                    }
                }
                this.mHistoricalRegistry.clearHistory(uid, packageName);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0026, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void uidRemoved(int r3) {
        /*
            r2 = this;
            monitor-enter(r2)
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r0 = r2.mUidStates     // Catch:{ all -> 0x0027 }
            int r0 = r0.indexOfKey(r3)     // Catch:{ all -> 0x0027 }
            if (r0 < 0) goto L_0x0025
            java.lang.Class<android.content.pm.PackageManagerInternal> r0 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)     // Catch:{ all -> 0x0027 }
            android.content.pm.PackageManagerInternal r0 = (android.content.pm.PackageManagerInternal) r0     // Catch:{ all -> 0x0027 }
            java.lang.String r0 = r0.getNameForUid(r3)     // Catch:{ all -> 0x0027 }
            boolean r1 = android.text.TextUtils.isEmpty(r0)     // Catch:{ all -> 0x0027 }
            if (r1 != 0) goto L_0x001d
            monitor-exit(r2)     // Catch:{ all -> 0x0027 }
            return
        L_0x001d:
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r1 = r2.mUidStates     // Catch:{ all -> 0x0027 }
            r1.remove(r3)     // Catch:{ all -> 0x0027 }
            r2.scheduleFastWriteLocked()     // Catch:{ all -> 0x0027 }
        L_0x0025:
            monitor-exit(r2)     // Catch:{ all -> 0x0027 }
            return
        L_0x0027:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0027 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.uidRemoved(int):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0056 A[Catch:{ all -> 0x00ef }] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00eb A[Catch:{ all -> 0x00bd, all -> 0x00fe }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateUidProcState(int r24, int r25) {
        /*
            r23 = this;
            r1 = r23
            r2 = r24
            r3 = r25
            monitor-enter(r23)
            r0 = 1
            com.android.server.appop.AppOpsService$UidState r4 = r1.getUidStateLocked(r2, r0)     // Catch:{ all -> 0x0102 }
            int[] r5 = PROCESS_STATE_TO_UID_STATE     // Catch:{ all -> 0x0102 }
            r5 = r5[r3]     // Catch:{ all -> 0x0102 }
            if (r4 == 0) goto L_0x00f2
            int r6 = r4.pendingState     // Catch:{ all -> 0x00ef }
            if (r6 == r5) goto L_0x00f2
            int r11 = r4.pendingState     // Catch:{ all -> 0x00ef }
            r4.pendingState = r5     // Catch:{ all -> 0x00ef }
            int r6 = r4.state     // Catch:{ all -> 0x00ef }
            if (r5 < r6) goto L_0x004f
            r6 = 400(0x190, float:5.6E-43)
            if (r5 > r6) goto L_0x0027
            int r7 = r4.state     // Catch:{ all -> 0x0102 }
            if (r7 <= r6) goto L_0x0027
            goto L_0x004f
        L_0x0027:
            long r7 = r4.pendingStateCommitTime     // Catch:{ all -> 0x0102 }
            r9 = 0
            int r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
            if (r7 != 0) goto L_0x0052
            int r7 = r4.state     // Catch:{ all -> 0x0102 }
            r8 = 200(0xc8, float:2.8E-43)
            if (r7 > r8) goto L_0x003a
            com.android.server.appop.AppOpsService$Constants r6 = r1.mConstants     // Catch:{ all -> 0x0102 }
            long r6 = r6.TOP_STATE_SETTLE_TIME     // Catch:{ all -> 0x0102 }
            goto L_0x0047
        L_0x003a:
            int r7 = r4.state     // Catch:{ all -> 0x0102 }
            if (r7 > r6) goto L_0x0043
            com.android.server.appop.AppOpsService$Constants r6 = r1.mConstants     // Catch:{ all -> 0x0102 }
            long r6 = r6.FG_SERVICE_STATE_SETTLE_TIME     // Catch:{ all -> 0x0102 }
            goto L_0x0047
        L_0x0043:
            com.android.server.appop.AppOpsService$Constants r6 = r1.mConstants     // Catch:{ all -> 0x0102 }
            long r6 = r6.BG_STATE_SETTLE_TIME     // Catch:{ all -> 0x0102 }
        L_0x0047:
            long r8 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0102 }
            long r8 = r8 + r6
            r4.pendingStateCommitTime = r8     // Catch:{ all -> 0x0102 }
            goto L_0x0052
        L_0x004f:
            r1.commitUidPendingStateLocked(r4)     // Catch:{ all -> 0x00ef }
        L_0x0052:
            int r6 = r4.startNesting     // Catch:{ all -> 0x00ef }
            if (r6 == 0) goto L_0x00eb
            long r6 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00ef }
            r13 = r6
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r6 = r4.pkgOps     // Catch:{ all -> 0x00ef }
            int r6 = r6.size()     // Catch:{ all -> 0x00ef }
            int r6 = r6 - r0
        L_0x0062:
            if (r6 < 0) goto L_0x00e6
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r7 = r4.pkgOps     // Catch:{ all -> 0x00ef }
            java.lang.Object r7 = r7.valueAt(r6)     // Catch:{ all -> 0x00ef }
            com.android.server.appop.AppOpsService$Ops r7 = (com.android.server.appop.AppOpsService.Ops) r7     // Catch:{ all -> 0x00ef }
            r15 = r7
            int r7 = r15.size()     // Catch:{ all -> 0x00ef }
            int r7 = r7 - r0
            r12 = r7
        L_0x0073:
            if (r12 < 0) goto L_0x00d8
            java.lang.Object r7 = r15.valueAt(r12)     // Catch:{ all -> 0x00ef }
            com.android.server.appop.AppOpsService$Op r7 = (com.android.server.appop.AppOpsService.Op) r7     // Catch:{ all -> 0x00ef }
            r10 = r7
            int r7 = r10.startNesting     // Catch:{ all -> 0x00ef }
            if (r7 <= 0) goto L_0x00c4
            long r7 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x00ef }
            long r0 = r10.startRealtime     // Catch:{ all -> 0x00c0 }
            long r0 = r7 - r0
            r9 = r23
            com.android.server.appop.HistoricalRegistry r7 = r9.mHistoricalRegistry     // Catch:{ all -> 0x00bd }
            int r8 = r10.op     // Catch:{ all -> 0x00bd }
            r19 = r4
            com.android.server.appop.AppOpsService$UidState r4 = r10.uidState     // Catch:{ all -> 0x00bd }
            int r4 = r4.uid     // Catch:{ all -> 0x00bd }
            r16 = r12
            java.lang.String r12 = r10.packageName     // Catch:{ all -> 0x00bd }
            r17 = 1
            r2 = r9
            r9 = r4
            r4 = r10
            r10 = r12
            r20 = r16
            r12 = r17
            r21 = r13
            r13 = r0
            r7.increaseOpAccessDuration(r8, r9, r10, r11, r12, r13)     // Catch:{ all -> 0x00fe }
            r18 = 1
            r12 = r4
            r13 = r21
            r7 = r15
            r15 = r0
            r17 = r11
            r12.finished(r13, r15, r17, r18)     // Catch:{ all -> 0x00fe }
            r8 = r21
            r4.startRealtime = r8     // Catch:{ all -> 0x00fe }
            r10 = 1
            r4.started(r8, r5, r10)     // Catch:{ all -> 0x00fe }
            goto L_0x00cd
        L_0x00bd:
            r0 = move-exception
            r2 = r9
            goto L_0x00ff
        L_0x00c0:
            r0 = move-exception
            r2 = r23
            goto L_0x00ff
        L_0x00c4:
            r2 = r1
            r19 = r4
            r4 = r10
            r20 = r12
            r8 = r13
            r7 = r15
            r10 = r0
        L_0x00cd:
            int r12 = r20 + -1
            r1 = r2
            r15 = r7
            r13 = r8
            r0 = r10
            r4 = r19
            r2 = r24
            goto L_0x0073
        L_0x00d8:
            r10 = r0
            r2 = r1
            r19 = r4
            r20 = r12
            r8 = r13
            r7 = r15
            int r6 = r6 + -1
            r2 = r24
            goto L_0x0062
        L_0x00e6:
            r2 = r1
            r19 = r4
            r8 = r13
            goto L_0x00f5
        L_0x00eb:
            r2 = r1
            r19 = r4
            goto L_0x00f5
        L_0x00ef:
            r0 = move-exception
            r2 = r1
            goto L_0x00ff
        L_0x00f2:
            r2 = r1
            r19 = r4
        L_0x00f5:
            com.android.server.AppOpsServiceState r0 = r2.mServiceState     // Catch:{ all -> 0x00fe }
            r1 = r24
            r0.updateProcessState(r1, r3)     // Catch:{ all -> 0x0106 }
            monitor-exit(r23)     // Catch:{ all -> 0x0106 }
            return
        L_0x00fe:
            r0 = move-exception
        L_0x00ff:
            r1 = r24
            goto L_0x0104
        L_0x0102:
            r0 = move-exception
            r1 = r2
        L_0x0104:
            monitor-exit(r23)     // Catch:{ all -> 0x0106 }
            throw r0
        L_0x0106:
            r0 = move-exception
            goto L_0x0104
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.updateUidProcState(int, int):void");
    }

    public void shutdown() {
        Slog.w(TAG, "Writing app ops before shutdown...");
        boolean doWrite = false;
        synchronized (this) {
            if (this.mWriteScheduled) {
                this.mWriteScheduled = false;
                doWrite = true;
            }
        }
        if (doWrite) {
            writeState();
        }
    }

    private ArrayList<AppOpsManager.OpEntry> collectOps(Ops pkgOps, int[] ops) {
        ArrayList<AppOpsManager.OpEntry> resOps = null;
        long elapsedNow = SystemClock.elapsedRealtime();
        if (ops == null) {
            resOps = new ArrayList<>();
            for (int j = 0; j < pkgOps.size(); j++) {
                resOps.add(getOpEntryForResult((Op) pkgOps.valueAt(j), elapsedNow));
            }
        } else {
            for (int i : ops) {
                Op curOp = (Op) pkgOps.get(i);
                if (curOp != null) {
                    if (resOps == null) {
                        resOps = new ArrayList<>();
                    }
                    resOps.add(getOpEntryForResult(curOp, elapsedNow));
                }
            }
        }
        return resOps;
    }

    private ArrayList<AppOpsManager.OpEntry> collectOps(SparseIntArray uidOps, int[] ops) {
        if (uidOps == null) {
            return null;
        }
        ArrayList<AppOpsManager.OpEntry> resOps = null;
        if (ops == null) {
            resOps = new ArrayList<>();
            for (int j = 0; j < uidOps.size(); j++) {
                resOps.add(new AppOpsManager.OpEntry(uidOps.keyAt(j), uidOps.valueAt(j)));
            }
        } else {
            for (int j2 = 0; j2 < ops.length; j2++) {
                if (uidOps.indexOfKey(ops[j2]) >= 0) {
                    if (resOps == null) {
                        resOps = new ArrayList<>();
                    }
                    resOps.add(new AppOpsManager.OpEntry(uidOps.keyAt(j2), uidOps.valueAt(j2)));
                }
            }
        }
        return resOps;
    }

    private static AppOpsManager.OpEntry getOpEntryForResult(Op op, long elapsedNow) {
        if (op.running) {
            op.continuing(elapsedNow - op.startRealtime, op.uidState.state, 1);
        }
        return new AppOpsManager.OpEntry(op.op, op.running, op.mode, op.mAccessTimes != null ? op.mAccessTimes.clone() : null, op.mRejectTimes != null ? op.mRejectTimes.clone() : null, op.mDurations != null ? op.mDurations.clone() : null, op.mProxyUids != null ? op.mProxyUids.clone() : null, op.mProxyPackageNames != null ? op.mProxyPackageNames.clone() : null);
    }

    public List<AppOpsManager.PackageOps> getPackagesForOps(int[] ops) {
        this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        ArrayList<AppOpsManager.PackageOps> res = null;
        synchronized (this) {
            int uidStateCount = this.mUidStates.size();
            for (int i = 0; i < uidStateCount; i++) {
                UidState uidState = this.mUidStates.valueAt(i);
                if (uidState.pkgOps != null) {
                    if (!uidState.pkgOps.isEmpty()) {
                        ArrayMap<String, Ops> packages = uidState.pkgOps;
                        int packageCount = packages.size();
                        for (int j = 0; j < packageCount; j++) {
                            Ops pkgOps = packages.valueAt(j);
                            ArrayList<AppOpsManager.OpEntry> resOps = collectOps(pkgOps, ops);
                            if (resOps != null) {
                                if (res == null) {
                                    res = new ArrayList<>();
                                }
                                res.add(new AppOpsManager.PackageOps(pkgOps.packageName, pkgOps.uidState.uid, resOps));
                            }
                        }
                    }
                }
            }
        }
        return res;
    }

    public List<AppOpsManager.PackageOps> getOpsForPackage(int uid, String packageName, int[] ops) {
        this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        String resolvedPackageName = resolvePackageName(uid, packageName);
        if (resolvedPackageName == null) {
            return Collections.emptyList();
        }
        synchronized (this) {
            Ops pkgOps = getOpsRawLocked(uid, resolvedPackageName, false, false);
            if (pkgOps == null) {
                return null;
            }
            ArrayList<AppOpsManager.OpEntry> resOps = collectOps(pkgOps, ops);
            if (resOps == null) {
                return null;
            }
            ArrayList<AppOpsManager.PackageOps> res = new ArrayList<>();
            res.add(new AppOpsManager.PackageOps(pkgOps.packageName, pkgOps.uidState.uid, resOps));
            return res;
        }
    }

    public void getHistoricalOps(int uid, String packageName, List<String> opNames, long beginTimeMillis, long endTimeMillis, int flags, RemoteCallback callback) {
        List<String> list = opNames;
        new AppOpsManager.HistoricalOpsRequest.Builder(beginTimeMillis, endTimeMillis).setUid(uid).setPackageName(packageName).setOpNames(list).setFlags(flags).build();
        Preconditions.checkNotNull(callback, "callback cannot be null");
        this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), "getHistoricalOps");
        this.mHistoricalRegistry.getHistoricalOps(uid, packageName, list != null ? (String[]) list.toArray(new String[opNames.size()]) : null, beginTimeMillis, endTimeMillis, flags, callback);
    }

    public void getHistoricalOpsFromDiskRaw(int uid, String packageName, List<String> opNames, long beginTimeMillis, long endTimeMillis, int flags, RemoteCallback callback) {
        List<String> list = opNames;
        new AppOpsManager.HistoricalOpsRequest.Builder(beginTimeMillis, endTimeMillis).setUid(uid).setPackageName(packageName).setOpNames(list).setFlags(flags).build();
        Preconditions.checkNotNull(callback, "callback cannot be null");
        this.mContext.enforcePermission("android.permission.MANAGE_APPOPS", Binder.getCallingPid(), Binder.getCallingUid(), "getHistoricalOps");
        this.mHistoricalRegistry.getHistoricalOpsFromDiskRaw(uid, packageName, list != null ? (String[]) list.toArray(new String[opNames.size()]) : null, beginTimeMillis, endTimeMillis, flags, callback);
    }

    public void reloadNonHistoricalState() {
        this.mContext.enforcePermission("android.permission.MANAGE_APPOPS", Binder.getCallingPid(), Binder.getCallingUid(), "reloadNonHistoricalState");
        writeState();
        readState();
    }

    public List<AppOpsManager.PackageOps> getUidOps(int uid, int[] ops) {
        this.mContext.enforcePermission("android.permission.GET_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        synchronized (this) {
            UidState uidState = getUidStateLocked(uid, false);
            if (uidState == null) {
                return null;
            }
            ArrayList<AppOpsManager.OpEntry> resOps = collectOps(uidState.opModes, ops);
            if (resOps == null) {
                return null;
            }
            ArrayList<AppOpsManager.PackageOps> res = new ArrayList<>();
            res.add(new AppOpsManager.PackageOps((String) null, uidState.uid, resOps));
            return res;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0018, code lost:
        r1 = r0.uidState;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void pruneOp(com.android.server.appop.AppOpsService.Op r5, int r6, java.lang.String r7) {
        /*
            r4 = this;
            boolean r0 = r5.hasAnyTime()
            if (r0 != 0) goto L_0x0037
            r0 = 0
            com.android.server.appop.AppOpsService$Ops r0 = r4.getOpsRawLocked(r6, r7, r0, r0)
            if (r0 == 0) goto L_0x0037
            int r1 = r5.op
            r0.remove(r1)
            int r1 = r0.size()
            if (r1 > 0) goto L_0x0037
            com.android.server.appop.AppOpsService$UidState r1 = r0.uidState
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r2 = r1.pkgOps
            if (r2 == 0) goto L_0x0037
            java.lang.String r3 = r0.packageName
            r2.remove(r3)
            boolean r3 = r2.isEmpty()
            if (r3 == 0) goto L_0x002c
            r3 = 0
            r1.pkgOps = r3
        L_0x002c:
            boolean r3 = r1.isDefault()
            if (r3 == 0) goto L_0x0037
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r3 = r4.mUidStates
            r3.remove(r6)
        L_0x0037:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.pruneOp(com.android.server.appop.AppOpsService$Op, int, java.lang.String):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0024, code lost:
        r6.mContext.enforcePermission("android.permission.MANAGE_APP_OPS_MODES", android.os.Binder.getCallingPid(), android.os.Binder.getCallingUid(), (java.lang.String) null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void enforceManageAppOpsModes(int r7, int r8, int r9) {
        /*
            r6 = this;
            int r0 = android.os.Process.myPid()
            if (r7 != r0) goto L_0x0007
            return
        L_0x0007:
            int r0 = android.os.UserHandle.getUserId(r8)
            monitor-enter(r6)
            android.util.SparseIntArray r1 = r6.mProfileOwners     // Catch:{ all -> 0x0035 }
            if (r1 == 0) goto L_0x0023
            android.util.SparseIntArray r1 = r6.mProfileOwners     // Catch:{ all -> 0x0035 }
            r2 = -1
            int r1 = r1.get(r0, r2)     // Catch:{ all -> 0x0035 }
            if (r1 != r8) goto L_0x0023
            if (r9 < 0) goto L_0x0023
            int r1 = android.os.UserHandle.getUserId(r9)     // Catch:{ all -> 0x0035 }
            if (r0 != r1) goto L_0x0023
            monitor-exit(r6)     // Catch:{ all -> 0x0035 }
            return
        L_0x0023:
            monitor-exit(r6)     // Catch:{ all -> 0x0035 }
            android.content.Context r1 = r6.mContext
            int r2 = android.os.Binder.getCallingPid()
            int r3 = android.os.Binder.getCallingUid()
            r4 = 0
            java.lang.String r5 = "android.permission.MANAGE_APP_OPS_MODES"
            r1.enforcePermission(r5, r2, r3, r4)
            return
        L_0x0035:
            r1 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0035 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.enforceManageAppOpsModes(int, int, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x008c, code lost:
        r12 = getPackagesForUid(r21);
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0091, code lost:
        monitor-enter(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        r0 = r7.mOpModeWatchers.get(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x009a, code lost:
        if (r0 == null) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x009c, code lost:
        r3 = r0.size();
        r4 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a1, code lost:
        if (r4 >= r3) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a3, code lost:
        r5 = r0.valueAt(r4);
        r6 = new android.util.ArraySet<>();
        java.util.Collections.addAll(r6, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00b1, code lost:
        if (r2 != null) goto L_0x00b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00b3, code lost:
        r2 = new android.util.ArrayMap<>();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00b9, code lost:
        r2.put(r5, r6);
        r4 = r4 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00c0, code lost:
        r3 = r12.length;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00c1, code lost:
        r13 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00c2, code lost:
        if (r1 >= r3) goto L_0x0101;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        r2 = r12[r1];
        r0 = r7.mPackageModeWatchers.get(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00cf, code lost:
        if (r0 == null) goto L_0x00fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00d1, code lost:
        if (r13 != null) goto L_0x00d9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00d3, code lost:
        r13 = new android.util.ArrayMap<>();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00d9, code lost:
        r4 = r0.size();
        r5 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00de, code lost:
        if (r5 >= r4) goto L_0x00fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00e0, code lost:
        r6 = r0.valueAt(r5);
        r14 = r13.get(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00ec, code lost:
        if (r14 != null) goto L_0x00f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00ee, code lost:
        r14 = new android.util.ArraySet<>();
        r13.put(r6, r14);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00f7, code lost:
        r14.add(r2);
        r5 = r5 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00fe, code lost:
        r1 = r1 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0101, code lost:
        monitor-exit(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x0102, code lost:
        if (r13 != null) goto L_0x0108;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x0104, code lost:
        notifyOpChangedSync(r10, r8, (java.lang.String) null, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0107, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0108, code lost:
        r0 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x010d, code lost:
        if (r0 >= r13.size()) goto L_0x0179;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x010f, code lost:
        r14 = r13.keyAt(r0);
        r15 = r13.valueAt(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x011d, code lost:
        if (r15 != null) goto L_0x013d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x011f, code lost:
        r7.mHandler.sendMessage(com.android.internal.util.function.pooled.PooledLambda.obtainMessage(com.android.server.appop.$$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs.INSTANCE, r19, r14, java.lang.Integer.valueOf(r10), java.lang.Integer.valueOf(r21), r11));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x013d, code lost:
        r11 = r15.size();
        r6 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0143, code lost:
        if (r6 >= r11) goto L_0x0171;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x0145, code lost:
        r18 = r11;
        r7.mHandler.sendMessage(com.android.internal.util.function.pooled.PooledLambda.obtainMessage(com.android.server.appop.$$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs.INSTANCE, r19, r14, java.lang.Integer.valueOf(r10), java.lang.Integer.valueOf(r21), r15.valueAt(r6)));
        r6 = r6 + 1;
        r11 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x0171, code lost:
        r17 = r6;
        r18 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0175, code lost:
        r0 = r0 + 1;
        r11 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0179, code lost:
        notifyOpChangedSync(r10, r8, (java.lang.String) null, r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x017d, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x017e, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x017f, code lost:
        r2 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0181, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:?, code lost:
        monitor-exit(r19);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0183, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setUidMode(int r20, int r21, int r22) {
        /*
            r19 = this;
            r7 = r19
            r8 = r21
            r9 = r22
            int r0 = android.os.Binder.getCallingPid()
            int r1 = android.os.Binder.getCallingUid()
            r7.enforceManageAppOpsModes(r0, r1, r8)
            r19.verifyIncomingOp(r20)
            int r10 = android.app.AppOpsManager.opToSwitch(r20)
            monitor-enter(r19)
            int r0 = android.app.AppOpsManager.opToDefaultMode(r10)     // Catch:{ all -> 0x0184 }
            r1 = 0
            com.android.server.appop.AppOpsService$UidState r2 = r7.getUidStateLocked(r8, r1)     // Catch:{ all -> 0x0184 }
            r11 = 0
            if (r2 != 0) goto L_0x0044
            if (r9 != r0) goto L_0x0029
            monitor-exit(r19)     // Catch:{ all -> 0x0184 }
            return
        L_0x0029:
            com.android.server.appop.AppOpsService$UidState r3 = new com.android.server.appop.AppOpsService$UidState     // Catch:{ all -> 0x0184 }
            r3.<init>(r8)     // Catch:{ all -> 0x0184 }
            r2 = r3
            android.util.SparseIntArray r3 = new android.util.SparseIntArray     // Catch:{ all -> 0x0184 }
            r3.<init>()     // Catch:{ all -> 0x0184 }
            r2.opModes = r3     // Catch:{ all -> 0x0184 }
            android.util.SparseIntArray r3 = r2.opModes     // Catch:{ all -> 0x0184 }
            r3.put(r10, r9)     // Catch:{ all -> 0x0184 }
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r3 = r7.mUidStates     // Catch:{ all -> 0x0184 }
            r3.put(r8, r2)     // Catch:{ all -> 0x0184 }
            r19.scheduleWriteLocked()     // Catch:{ all -> 0x0184 }
            goto L_0x0086
        L_0x0044:
            android.util.SparseIntArray r3 = r2.opModes     // Catch:{ all -> 0x0184 }
            if (r3 != 0) goto L_0x005a
            if (r9 == r0) goto L_0x0086
            android.util.SparseIntArray r3 = new android.util.SparseIntArray     // Catch:{ all -> 0x0184 }
            r3.<init>()     // Catch:{ all -> 0x0184 }
            r2.opModes = r3     // Catch:{ all -> 0x0184 }
            android.util.SparseIntArray r3 = r2.opModes     // Catch:{ all -> 0x0184 }
            r3.put(r10, r9)     // Catch:{ all -> 0x0184 }
            r19.scheduleWriteLocked()     // Catch:{ all -> 0x0184 }
            goto L_0x0086
        L_0x005a:
            android.util.SparseIntArray r3 = r2.opModes     // Catch:{ all -> 0x0184 }
            int r3 = r3.indexOfKey(r10)     // Catch:{ all -> 0x0184 }
            if (r3 < 0) goto L_0x006c
            android.util.SparseIntArray r3 = r2.opModes     // Catch:{ all -> 0x0184 }
            int r3 = r3.get(r10)     // Catch:{ all -> 0x0184 }
            if (r3 != r9) goto L_0x006c
            monitor-exit(r19)     // Catch:{ all -> 0x0184 }
            return
        L_0x006c:
            if (r9 != r0) goto L_0x007e
            android.util.SparseIntArray r3 = r2.opModes     // Catch:{ all -> 0x0184 }
            r3.delete(r10)     // Catch:{ all -> 0x0184 }
            android.util.SparseIntArray r3 = r2.opModes     // Catch:{ all -> 0x0184 }
            int r3 = r3.size()     // Catch:{ all -> 0x0184 }
            if (r3 > 0) goto L_0x0083
            r2.opModes = r11     // Catch:{ all -> 0x0184 }
            goto L_0x0083
        L_0x007e:
            android.util.SparseIntArray r3 = r2.opModes     // Catch:{ all -> 0x0184 }
            r3.put(r10, r9)     // Catch:{ all -> 0x0184 }
        L_0x0083:
            r19.scheduleWriteLocked()     // Catch:{ all -> 0x0184 }
        L_0x0086:
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r3 = r7.mOpModeWatchers     // Catch:{ all -> 0x0184 }
            r2.evalForegroundOps(r3)     // Catch:{ all -> 0x0184 }
            monitor-exit(r19)     // Catch:{ all -> 0x0184 }
            java.lang.String[] r12 = getPackagesForUid(r21)
            r2 = 0
            monitor-enter(r19)
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r0 = r7.mOpModeWatchers     // Catch:{ all -> 0x0181 }
            java.lang.Object r0 = r0.get(r10)     // Catch:{ all -> 0x0181 }
            android.util.ArraySet r0 = (android.util.ArraySet) r0     // Catch:{ all -> 0x0181 }
            if (r0 == 0) goto L_0x00c0
            int r3 = r0.size()     // Catch:{ all -> 0x0181 }
            r4 = 0
        L_0x00a1:
            if (r4 >= r3) goto L_0x00c0
            java.lang.Object r5 = r0.valueAt(r4)     // Catch:{ all -> 0x0181 }
            com.android.server.appop.AppOpsService$ModeCallback r5 = (com.android.server.appop.AppOpsService.ModeCallback) r5     // Catch:{ all -> 0x0181 }
            android.util.ArraySet r6 = new android.util.ArraySet     // Catch:{ all -> 0x0181 }
            r6.<init>()     // Catch:{ all -> 0x0181 }
            java.util.Collections.addAll(r6, r12)     // Catch:{ all -> 0x0181 }
            if (r2 != 0) goto L_0x00b9
            android.util.ArrayMap r13 = new android.util.ArrayMap     // Catch:{ all -> 0x0181 }
            r13.<init>()     // Catch:{ all -> 0x0181 }
            r2 = r13
        L_0x00b9:
            r2.put(r5, r6)     // Catch:{ all -> 0x0181 }
            int r4 = r4 + 1
            goto L_0x00a1
        L_0x00c0:
            int r3 = r12.length     // Catch:{ all -> 0x0181 }
            r13 = r2
        L_0x00c2:
            if (r1 >= r3) goto L_0x0101
            r2 = r12[r1]     // Catch:{ all -> 0x017e }
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r4 = r7.mPackageModeWatchers     // Catch:{ all -> 0x017e }
            java.lang.Object r4 = r4.get(r2)     // Catch:{ all -> 0x017e }
            android.util.ArraySet r4 = (android.util.ArraySet) r4     // Catch:{ all -> 0x017e }
            r0 = r4
            if (r0 == 0) goto L_0x00fe
            if (r13 != 0) goto L_0x00d9
            android.util.ArrayMap r4 = new android.util.ArrayMap     // Catch:{ all -> 0x017e }
            r4.<init>()     // Catch:{ all -> 0x017e }
            r13 = r4
        L_0x00d9:
            int r4 = r0.size()     // Catch:{ all -> 0x017e }
            r5 = 0
        L_0x00de:
            if (r5 >= r4) goto L_0x00fe
            java.lang.Object r6 = r0.valueAt(r5)     // Catch:{ all -> 0x017e }
            com.android.server.appop.AppOpsService$ModeCallback r6 = (com.android.server.appop.AppOpsService.ModeCallback) r6     // Catch:{ all -> 0x017e }
            java.lang.Object r14 = r13.get(r6)     // Catch:{ all -> 0x017e }
            android.util.ArraySet r14 = (android.util.ArraySet) r14     // Catch:{ all -> 0x017e }
            if (r14 != 0) goto L_0x00f7
            android.util.ArraySet r15 = new android.util.ArraySet     // Catch:{ all -> 0x017e }
            r15.<init>()     // Catch:{ all -> 0x017e }
            r14 = r15
            r13.put(r6, r14)     // Catch:{ all -> 0x017e }
        L_0x00f7:
            r14.add(r2)     // Catch:{ all -> 0x017e }
            int r5 = r5 + 1
            goto L_0x00de
        L_0x00fe:
            int r1 = r1 + 1
            goto L_0x00c2
        L_0x0101:
            monitor-exit(r19)     // Catch:{ all -> 0x017e }
            if (r13 != 0) goto L_0x0108
            r7.notifyOpChangedSync(r10, r8, r11, r9)
            return
        L_0x0108:
            r0 = 0
        L_0x0109:
            int r1 = r13.size()
            if (r0 >= r1) goto L_0x0179
            java.lang.Object r1 = r13.keyAt(r0)
            r14 = r1
            com.android.server.appop.AppOpsService$ModeCallback r14 = (com.android.server.appop.AppOpsService.ModeCallback) r14
            java.lang.Object r1 = r13.valueAt(r0)
            r15 = r1
            android.util.ArraySet r15 = (android.util.ArraySet) r15
            if (r15 != 0) goto L_0x013d
            android.os.Handler r6 = r7.mHandler
            com.android.server.appop.-$$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs r1 = com.android.server.appop.$$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs.INSTANCE
            java.lang.Integer r4 = java.lang.Integer.valueOf(r10)
            java.lang.Integer r5 = java.lang.Integer.valueOf(r21)
            r16 = r11
            java.lang.String r16 = (java.lang.String) r16
            r2 = r19
            r3 = r14
            r11 = r6
            r6 = r16
            android.os.Message r1 = com.android.internal.util.function.pooled.PooledLambda.obtainMessage(r1, r2, r3, r4, r5, r6)
            r11.sendMessage(r1)
            goto L_0x0175
        L_0x013d:
            int r11 = r15.size()
            r1 = 0
            r6 = r1
        L_0x0143:
            if (r6 >= r11) goto L_0x0171
            java.lang.Object r1 = r15.valueAt(r6)
            r16 = r1
            java.lang.String r16 = (java.lang.String) r16
            android.os.Handler r5 = r7.mHandler
            com.android.server.appop.-$$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs r1 = com.android.server.appop.$$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs.INSTANCE
            java.lang.Integer r4 = java.lang.Integer.valueOf(r10)
            java.lang.Integer r17 = java.lang.Integer.valueOf(r21)
            r2 = r19
            r3 = r14
            r18 = r11
            r11 = r5
            r5 = r17
            r17 = r6
            r6 = r16
            android.os.Message r1 = com.android.internal.util.function.pooled.PooledLambda.obtainMessage(r1, r2, r3, r4, r5, r6)
            r11.sendMessage(r1)
            int r6 = r17 + 1
            r11 = r18
            goto L_0x0143
        L_0x0171:
            r17 = r6
            r18 = r11
        L_0x0175:
            int r0 = r0 + 1
            r11 = 0
            goto L_0x0109
        L_0x0179:
            r0 = 0
            r7.notifyOpChangedSync(r10, r8, r0, r9)
            return
        L_0x017e:
            r0 = move-exception
            r2 = r13
            goto L_0x0182
        L_0x0181:
            r0 = move-exception
        L_0x0182:
            monitor-exit(r19)     // Catch:{ all -> 0x0181 }
            throw r0
        L_0x0184:
            r0 = move-exception
            monitor-exit(r19)     // Catch:{ all -> 0x0184 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.setUidMode(int, int, int):void");
    }

    private void notifyOpChangedSync(int code, int uid, String packageName, int mode) {
        StorageManagerInternal storageManagerInternal = (StorageManagerInternal) LocalServices.getService(StorageManagerInternal.class);
        if (storageManagerInternal != null) {
            storageManagerInternal.onAppOpsChanged(code, uid, packageName, mode == 4 ? 0 : mode);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x003e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setAllPkgModesToDefault(int r10, int r11) {
        /*
            r9 = this;
            monitor-enter(r9)
            r0 = 0
            com.android.server.appop.AppOpsService$UidState r0 = r9.getUidStateLocked(r11, r0)     // Catch:{ all -> 0x003f }
            if (r0 != 0) goto L_0x000a
            monitor-exit(r9)     // Catch:{ all -> 0x003f }
            return
        L_0x000a:
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r1 = r0.pkgOps     // Catch:{ all -> 0x003f }
            if (r1 != 0) goto L_0x0010
            monitor-exit(r9)     // Catch:{ all -> 0x003f }
            return
        L_0x0010:
            r2 = 0
            int r3 = r1.size()     // Catch:{ all -> 0x003f }
            r4 = 0
        L_0x0016:
            if (r4 >= r3) goto L_0x0038
            java.lang.Object r5 = r1.valueAt(r4)     // Catch:{ all -> 0x003f }
            com.android.server.appop.AppOpsService$Ops r5 = (com.android.server.appop.AppOpsService.Ops) r5     // Catch:{ all -> 0x003f }
            java.lang.Object r6 = r5.get(r10)     // Catch:{ all -> 0x003f }
            com.android.server.appop.AppOpsService$Op r6 = (com.android.server.appop.AppOpsService.Op) r6     // Catch:{ all -> 0x003f }
            if (r6 != 0) goto L_0x0027
            goto L_0x0035
        L_0x0027:
            int r7 = android.app.AppOpsManager.opToDefaultMode(r10)     // Catch:{ all -> 0x003f }
            int r8 = r6.mode     // Catch:{ all -> 0x003f }
            if (r8 == r7) goto L_0x0035
            int unused = r6.mode = r7     // Catch:{ all -> 0x003f }
            r2 = 1
        L_0x0035:
            int r4 = r4 + 1
            goto L_0x0016
        L_0x0038:
            if (r2 == 0) goto L_0x003d
            r9.scheduleWriteLocked()     // Catch:{ all -> 0x003f }
        L_0x003d:
            monitor-exit(r9)     // Catch:{ all -> 0x003f }
            return
        L_0x003f:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x003f }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.setAllPkgModesToDefault(int, int):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0084, code lost:
        if (r1 == null) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0086, code lost:
        r2 = r11;
        r12.mHandler.sendMessage(com.android.internal.util.function.pooled.PooledLambda.obtainMessage(com.android.server.appop.$$Lambda$AppOpsService$NDUi03ZZuuR42RDEIQ0UELKycc.INSTANCE, r16, r1, java.lang.Integer.valueOf(r11), java.lang.Integer.valueOf(r18), r19));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00a0, code lost:
        r2 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00a1, code lost:
        notifyOpChangedSync(r2, r13, r14, r15);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a4, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMode(int r17, int r18, java.lang.String r19, int r20) {
        /*
            r16 = this;
            r12 = r16
            r13 = r18
            r14 = r19
            r15 = r20
            int r0 = android.os.Binder.getCallingPid()
            int r1 = android.os.Binder.getCallingUid()
            r12.enforceManageAppOpsModes(r0, r1, r13)
            r16.verifyIncomingOp(r17)
            r7 = 0
            int r11 = android.app.AppOpsManager.opToSwitch(r17)
            boolean r5 = r12.verifyAndGetIsPrivileged(r13, r14)     // Catch:{ SecurityException -> 0x00af }
            monitor-enter(r16)
            r0 = 0
            com.android.server.appop.AppOpsService$UidState r0 = r12.getUidStateLocked(r13, r0)     // Catch:{ all -> 0x00a9 }
            r6 = 1
            r1 = r16
            r2 = r11
            r3 = r18
            r4 = r19
            com.android.server.appop.AppOpsService$Op r1 = r1.getOpLocked(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x00a9 }
            if (r1 == 0) goto L_0x0082
            int r2 = r1.mode     // Catch:{ all -> 0x007f }
            if (r2 == r15) goto L_0x0082
            int unused = r1.mode = r15     // Catch:{ all -> 0x007f }
            if (r0 == 0) goto L_0x0044
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r2 = r12.mOpModeWatchers     // Catch:{ all -> 0x007f }
            r0.evalForegroundOps(r2)     // Catch:{ all -> 0x007f }
        L_0x0044:
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r2 = r12.mOpModeWatchers     // Catch:{ all -> 0x007f }
            java.lang.Object r2 = r2.get(r11)     // Catch:{ all -> 0x007f }
            android.util.ArraySet r2 = (android.util.ArraySet) r2     // Catch:{ all -> 0x007f }
            if (r2 == 0) goto L_0x0059
            if (r7 != 0) goto L_0x0056
            android.util.ArraySet r3 = new android.util.ArraySet     // Catch:{ all -> 0x007f }
            r3.<init>()     // Catch:{ all -> 0x007f }
            r7 = r3
        L_0x0056:
            r7.addAll(r2)     // Catch:{ all -> 0x007f }
        L_0x0059:
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r3 = r12.mPackageModeWatchers     // Catch:{ all -> 0x007f }
            java.lang.Object r3 = r3.get(r14)     // Catch:{ all -> 0x007f }
            android.util.ArraySet r3 = (android.util.ArraySet) r3     // Catch:{ all -> 0x007f }
            r2 = r3
            if (r2 == 0) goto L_0x006f
            if (r7 != 0) goto L_0x006c
            android.util.ArraySet r3 = new android.util.ArraySet     // Catch:{ all -> 0x007f }
            r3.<init>()     // Catch:{ all -> 0x007f }
            r7 = r3
        L_0x006c:
            r7.addAll(r2)     // Catch:{ all -> 0x007f }
        L_0x006f:
            int r3 = r1.op     // Catch:{ all -> 0x007f }
            int r3 = android.app.AppOpsManager.opToDefaultMode(r3)     // Catch:{ all -> 0x007f }
            if (r15 != r3) goto L_0x007a
            r12.pruneOp(r1, r13, r14)     // Catch:{ all -> 0x007f }
        L_0x007a:
            r16.scheduleFastWriteLocked()     // Catch:{ all -> 0x007f }
            r1 = r7
            goto L_0x0083
        L_0x007f:
            r0 = move-exception
            r2 = r11
            goto L_0x00ab
        L_0x0082:
            r1 = r7
        L_0x0083:
            monitor-exit(r16)     // Catch:{ all -> 0x00a5 }
            if (r1 == 0) goto L_0x00a0
            android.os.Handler r0 = r12.mHandler
            com.android.server.appop.-$$Lambda$AppOpsService$NDUi03ZZuuR42-RDEIQ0UELKycc r6 = com.android.server.appop.$$Lambda$AppOpsService$NDUi03ZZuuR42RDEIQ0UELKycc.INSTANCE
            java.lang.Integer r9 = java.lang.Integer.valueOf(r11)
            java.lang.Integer r10 = java.lang.Integer.valueOf(r18)
            r7 = r16
            r8 = r1
            r2 = r11
            r11 = r19
            android.os.Message r3 = com.android.internal.util.function.pooled.PooledLambda.obtainMessage(r6, r7, r8, r9, r10, r11)
            r0.sendMessage(r3)
            goto L_0x00a1
        L_0x00a0:
            r2 = r11
        L_0x00a1:
            r12.notifyOpChangedSync(r2, r13, r14, r15)
            return
        L_0x00a5:
            r0 = move-exception
            r2 = r11
            r7 = r1
            goto L_0x00ab
        L_0x00a9:
            r0 = move-exception
            r2 = r11
        L_0x00ab:
            monitor-exit(r16)     // Catch:{ all -> 0x00ad }
            throw r0
        L_0x00ad:
            r0 = move-exception
            goto L_0x00ab
        L_0x00af:
            r0 = move-exception
            r2 = r11
            r1 = r0
            r0 = r1
            java.lang.String r1 = "AppOps"
            java.lang.String r3 = "Cannot setMode"
            android.util.Slog.e(r1, r3, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.setMode(int, int, java.lang.String, int):void");
    }

    /* access modifiers changed from: private */
    public void notifyOpChanged(ArraySet<ModeCallback> callbacks, int code, int uid, String packageName) {
        for (int i = 0; i < callbacks.size(); i++) {
            notifyOpChanged(callbacks.valueAt(i), code, uid, packageName);
        }
    }

    /* access modifiers changed from: private */
    public void notifyOpChanged(ModeCallback callback, int code, int uid, String packageName) {
        if (uid == -2 || callback.mWatchingUid < 0 || callback.mWatchingUid == uid) {
            long identity = Binder.clearCallingIdentity();
            try {
                callback.mCallback.opChanged(code, uid, packageName);
            } catch (RemoteException e) {
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
            Binder.restoreCallingIdentity(identity);
        }
    }

    private static HashMap<ModeCallback, ArrayList<ChangeRec>> addCallbacks(HashMap<ModeCallback, ArrayList<ChangeRec>> callbacks, int op, int uid, String packageName, ArraySet<ModeCallback> cbs) {
        if (cbs == null) {
            return callbacks;
        }
        if (callbacks == null) {
            callbacks = new HashMap<>();
        }
        boolean duplicate = false;
        int N = cbs.size();
        for (int i = 0; i < N; i++) {
            ModeCallback cb = cbs.valueAt(i);
            ArrayList<ChangeRec> reports = callbacks.get(cb);
            if (reports != null) {
                int reportCount = reports.size();
                int j = 0;
                while (true) {
                    if (j >= reportCount) {
                        break;
                    }
                    ChangeRec report = reports.get(j);
                    if (report.op == op && report.pkg.equals(packageName)) {
                        duplicate = true;
                        break;
                    }
                    j++;
                }
            } else {
                reports = new ArrayList<>();
                callbacks.put(cb, reports);
            }
            if (!duplicate) {
                reports.add(new ChangeRec(op, uid, packageName));
            }
        }
        return callbacks;
    }

    static final class ChangeRec {
        final int op;
        final String pkg;
        final int uid;

        ChangeRec(int _op, int _uid, String _pkg) {
            this.op = _op;
            this.uid = _uid;
            this.pkg = _pkg;
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public void resetAllModes(int r23, java.lang.String r24) {
        /*
            r22 = this;
            r7 = r22
            r8 = r24
            int r6 = android.os.Binder.getCallingPid()
            int r5 = android.os.Binder.getCallingUid()
            r12 = 1
            r13 = 1
            java.lang.String r14 = "resetAllModes"
            r15 = 0
            r9 = r6
            r10 = r5
            r11 = r23
            int r9 = android.app.ActivityManager.handleIncomingUser(r9, r10, r11, r12, r13, r14, r15)
            r1 = -1
            if (r8 == 0) goto L_0x002b
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x002a }
            r2 = 8192(0x2000, float:1.14794E-41)
            int r0 = r0.getPackageUid(r8, r2, r9)     // Catch:{ RemoteException -> 0x002a }
            r1 = r0
            r10 = r1
            goto L_0x002c
        L_0x002a:
            r0 = move-exception
        L_0x002b:
            r10 = r1
        L_0x002c:
            r7.enforceManageAppOpsModes(r6, r5, r10)
            r1 = 0
            monitor-enter(r22)
            r0 = 0
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r2 = r7.mUidStates     // Catch:{ all -> 0x0287 }
            int r2 = r2.size()     // Catch:{ all -> 0x0287 }
            int r2 = r2 + -1
            r11 = r1
        L_0x003b:
            if (r2 < 0) goto L_0x01f8
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r1 = r7.mUidStates     // Catch:{ all -> 0x01f0 }
            java.lang.Object r1 = r1.valueAt(r2)     // Catch:{ all -> 0x01f0 }
            com.android.server.appop.AppOpsService$UidState r1 = (com.android.server.appop.AppOpsService.UidState) r1     // Catch:{ all -> 0x01f0 }
            android.util.SparseIntArray r3 = r1.opModes     // Catch:{ all -> 0x01f0 }
            r12 = -1
            if (r3 == 0) goto L_0x00f6
            int r13 = r1.uid     // Catch:{ all -> 0x01f0 }
            if (r13 == r10) goto L_0x0059
            if (r10 != r12) goto L_0x0051
            goto L_0x0059
        L_0x0051:
            r17 = r3
            r20 = r5
            r21 = r6
            goto L_0x00fc
        L_0x0059:
            int r13 = r3.size()     // Catch:{ all -> 0x01f0 }
            int r14 = r13 + -1
        L_0x005f:
            if (r14 < 0) goto L_0x00ef
            int r15 = r3.keyAt(r14)     // Catch:{ all -> 0x01f0 }
            boolean r16 = android.app.AppOpsManager.opAllowsReset(r15)     // Catch:{ all -> 0x01f0 }
            if (r16 == 0) goto L_0x00de
            r3.removeAt(r14)     // Catch:{ all -> 0x01f0 }
            int r16 = r3.size()     // Catch:{ all -> 0x01f0 }
            if (r16 > 0) goto L_0x0080
            r4 = 0
            r1.opModes = r4     // Catch:{ all -> 0x0078 }
            goto L_0x0080
        L_0x0078:
            r0 = move-exception
            r18 = r5
            r16 = r6
            r1 = r11
            goto L_0x028c
        L_0x0080:
            int r4 = r1.uid     // Catch:{ all -> 0x01f0 }
            java.lang.String[] r4 = getPackagesForUid(r4)     // Catch:{ all -> 0x01f0 }
            int r12 = r4.length     // Catch:{ all -> 0x01f0 }
            r17 = r3
            r3 = r11
            r11 = 0
        L_0x008b:
            if (r11 >= r12) goto L_0x00d8
            r18 = r4[r11]     // Catch:{ all -> 0x00d0 }
            r19 = r18
            r18 = r4
            int r4 = r1.uid     // Catch:{ all -> 0x00d0 }
            r20 = r5
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r5 = r7.mOpModeWatchers     // Catch:{ all -> 0x00c8 }
            java.lang.Object r5 = r5.get(r15)     // Catch:{ all -> 0x00c8 }
            android.util.ArraySet r5 = (android.util.ArraySet) r5     // Catch:{ all -> 0x00c8 }
            r21 = r6
            r6 = r19
            java.util.HashMap r4 = addCallbacks(r3, r15, r4, r6, r5)     // Catch:{ all -> 0x00c0 }
            r3 = r4
            int r4 = r1.uid     // Catch:{ all -> 0x00c0 }
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r5 = r7.mPackageModeWatchers     // Catch:{ all -> 0x00c0 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x00c0 }
            android.util.ArraySet r5 = (android.util.ArraySet) r5     // Catch:{ all -> 0x00c0 }
            java.util.HashMap r4 = addCallbacks(r3, r15, r4, r6, r5)     // Catch:{ all -> 0x00c0 }
            r3 = r4
            int r11 = r11 + 1
            r4 = r18
            r5 = r20
            r6 = r21
            goto L_0x008b
        L_0x00c0:
            r0 = move-exception
            r1 = r3
            r18 = r20
            r16 = r21
            goto L_0x028c
        L_0x00c8:
            r0 = move-exception
            r1 = r3
            r16 = r6
            r18 = r20
            goto L_0x028c
        L_0x00d0:
            r0 = move-exception
            r1 = r3
            r18 = r5
            r16 = r6
            goto L_0x028c
        L_0x00d8:
            r20 = r5
            r21 = r6
            r11 = r3
            goto L_0x00e4
        L_0x00de:
            r17 = r3
            r20 = r5
            r21 = r6
        L_0x00e4:
            int r14 = r14 + -1
            r3 = r17
            r5 = r20
            r6 = r21
            r12 = -1
            goto L_0x005f
        L_0x00ef:
            r17 = r3
            r20 = r5
            r21 = r6
            goto L_0x00fc
        L_0x00f6:
            r17 = r3
            r20 = r5
            r21 = r6
        L_0x00fc:
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r3 = r1.pkgOps     // Catch:{ all -> 0x0202 }
            if (r3 != 0) goto L_0x0102
            goto L_0x01e8
        L_0x0102:
            r3 = -1
            if (r9 == r3) goto L_0x010f
            int r3 = r1.uid     // Catch:{ all -> 0x0202 }
            int r3 = android.os.UserHandle.getUserId(r3)     // Catch:{ all -> 0x0202 }
            if (r9 == r3) goto L_0x010f
            goto L_0x01e8
        L_0x010f:
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r3 = r1.pkgOps     // Catch:{ all -> 0x0202 }
            java.util.Set r4 = r3.entrySet()     // Catch:{ all -> 0x0202 }
            java.util.Iterator r4 = r4.iterator()     // Catch:{ all -> 0x0202 }
            r5 = 0
        L_0x011a:
            boolean r6 = r4.hasNext()     // Catch:{ all -> 0x0202 }
            if (r6 == 0) goto L_0x01d2
            java.lang.Object r6 = r4.next()     // Catch:{ all -> 0x0202 }
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6     // Catch:{ all -> 0x0202 }
            java.lang.Object r12 = r6.getKey()     // Catch:{ all -> 0x0202 }
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ all -> 0x0202 }
            if (r8 == 0) goto L_0x0135
            boolean r13 = r8.equals(r12)     // Catch:{ all -> 0x0202 }
            if (r13 != 0) goto L_0x0135
            goto L_0x011a
        L_0x0135:
            java.lang.Object r13 = r6.getValue()     // Catch:{ all -> 0x0202 }
            com.android.server.appop.AppOpsService$Ops r13 = (com.android.server.appop.AppOpsService.Ops) r13     // Catch:{ all -> 0x0202 }
            int r14 = r13.size()     // Catch:{ all -> 0x0202 }
            int r14 = r14 + -1
        L_0x0141:
            if (r14 < 0) goto L_0x01bd
            java.lang.Object r15 = r13.valueAt(r14)     // Catch:{ all -> 0x0202 }
            com.android.server.appop.AppOpsService$Op r15 = (com.android.server.appop.AppOpsService.Op) r15     // Catch:{ all -> 0x0202 }
            r23 = r0
            int r0 = r15.op     // Catch:{ all -> 0x0202 }
            boolean r0 = android.app.AppOpsManager.opAllowsReset(r0)     // Catch:{ all -> 0x0202 }
            if (r0 == 0) goto L_0x01b0
            int r0 = r15.mode     // Catch:{ all -> 0x0202 }
            r16 = r3
            int r3 = r15.op     // Catch:{ all -> 0x0202 }
            int r3 = android.app.AppOpsManager.opToDefaultMode(r3)     // Catch:{ all -> 0x0202 }
            if (r0 == r3) goto L_0x01ad
            int r0 = r15.op     // Catch:{ all -> 0x0202 }
            int r0 = android.app.AppOpsManager.opToDefaultMode(r0)     // Catch:{ all -> 0x0202 }
            int unused = r15.mode = r0     // Catch:{ all -> 0x0202 }
            r0 = 1
            r5 = 1
            com.android.server.appop.AppOpsService$UidState r3 = r15.uidState     // Catch:{ all -> 0x0202 }
            int r3 = r3.uid     // Catch:{ all -> 0x0202 }
            r23 = r0
            int r0 = r15.op     // Catch:{ all -> 0x0202 }
            r18 = r5
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r5 = r7.mOpModeWatchers     // Catch:{ all -> 0x0202 }
            r19 = r6
            int r6 = r15.op     // Catch:{ all -> 0x0202 }
            java.lang.Object r5 = r5.get(r6)     // Catch:{ all -> 0x0202 }
            android.util.ArraySet r5 = (android.util.ArraySet) r5     // Catch:{ all -> 0x0202 }
            java.util.HashMap r0 = addCallbacks(r11, r0, r3, r12, r5)     // Catch:{ all -> 0x0202 }
            r5 = r0
            int r0 = r15.op     // Catch:{ all -> 0x01a5 }
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r6 = r7.mPackageModeWatchers     // Catch:{ all -> 0x01a5 }
            java.lang.Object r6 = r6.get(r12)     // Catch:{ all -> 0x01a5 }
            android.util.ArraySet r6 = (android.util.ArraySet) r6     // Catch:{ all -> 0x01a5 }
            java.util.HashMap r0 = addCallbacks(r5, r0, r3, r12, r6)     // Catch:{ all -> 0x01a5 }
            r5 = r0
            boolean r0 = r15.hasAnyTime()     // Catch:{ all -> 0x01a5 }
            if (r0 != 0) goto L_0x019f
            r13.removeAt(r14)     // Catch:{ all -> 0x01a5 }
        L_0x019f:
            r0 = r23
            r11 = r5
            r5 = r18
            goto L_0x01b6
        L_0x01a5:
            r0 = move-exception
            r1 = r5
            r18 = r20
            r16 = r21
            goto L_0x028c
        L_0x01ad:
            r19 = r6
            goto L_0x01b4
        L_0x01b0:
            r16 = r3
            r19 = r6
        L_0x01b4:
            r0 = r23
        L_0x01b6:
            int r14 = r14 + -1
            r3 = r16
            r6 = r19
            goto L_0x0141
        L_0x01bd:
            r23 = r0
            r16 = r3
            r19 = r6
            int r0 = r13.size()     // Catch:{ all -> 0x0202 }
            if (r0 != 0) goto L_0x01cc
            r4.remove()     // Catch:{ all -> 0x0202 }
        L_0x01cc:
            r0 = r23
            r3 = r16
            goto L_0x011a
        L_0x01d2:
            r16 = r3
            boolean r3 = r1.isDefault()     // Catch:{ all -> 0x0202 }
            if (r3 == 0) goto L_0x01e1
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r3 = r7.mUidStates     // Catch:{ all -> 0x0202 }
            int r6 = r1.uid     // Catch:{ all -> 0x0202 }
            r3.remove(r6)     // Catch:{ all -> 0x0202 }
        L_0x01e1:
            if (r5 == 0) goto L_0x01e8
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r3 = r7.mOpModeWatchers     // Catch:{ all -> 0x0202 }
            r1.evalForegroundOps(r3)     // Catch:{ all -> 0x0202 }
        L_0x01e8:
            int r2 = r2 + -1
            r5 = r20
            r6 = r21
            goto L_0x003b
        L_0x01f0:
            r0 = move-exception
            r18 = r5
            r16 = r6
            r1 = r11
            goto L_0x028c
        L_0x01f8:
            r20 = r5
            r21 = r6
            if (r0 == 0) goto L_0x020a
            r22.scheduleFastWriteLocked()     // Catch:{ all -> 0x0202 }
            goto L_0x020a
        L_0x0202:
            r0 = move-exception
            r1 = r11
            r18 = r20
            r16 = r21
            goto L_0x028c
        L_0x020a:
            monitor-exit(r22)     // Catch:{ all -> 0x0280 }
            if (r11 == 0) goto L_0x027b
            java.util.Set r0 = r11.entrySet()
            java.util.Iterator r0 = r0.iterator()
        L_0x0215:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0276
            java.lang.Object r1 = r0.next()
            r12 = r1
            java.util.Map$Entry r12 = (java.util.Map.Entry) r12
            java.lang.Object r1 = r12.getKey()
            r13 = r1
            com.android.server.appop.AppOpsService$ModeCallback r13 = (com.android.server.appop.AppOpsService.ModeCallback) r13
            java.lang.Object r1 = r12.getValue()
            r14 = r1
            java.util.ArrayList r14 = (java.util.ArrayList) r14
            r1 = 0
            r15 = r1
        L_0x0232:
            int r1 = r14.size()
            if (r15 >= r1) goto L_0x026f
            java.lang.Object r1 = r14.get(r15)
            r6 = r1
            com.android.server.appop.AppOpsService$ChangeRec r6 = (com.android.server.appop.AppOpsService.ChangeRec) r6
            android.os.Handler r5 = r7.mHandler
            com.android.server.appop.-$$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs r1 = com.android.server.appop.$$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs.INSTANCE
            int r2 = r6.op
            java.lang.Integer r4 = java.lang.Integer.valueOf(r2)
            int r2 = r6.uid
            java.lang.Integer r16 = java.lang.Integer.valueOf(r2)
            java.lang.String r3 = r6.pkg
            r2 = r22
            r17 = r3
            r3 = r13
            r23 = r0
            r0 = r5
            r18 = r20
            r5 = r16
            r19 = r6
            r16 = r21
            r6 = r17
            android.os.Message r1 = com.android.internal.util.function.pooled.PooledLambda.obtainMessage(r1, r2, r3, r4, r5, r6)
            r0.sendMessage(r1)
            int r15 = r15 + 1
            r0 = r23
            goto L_0x0232
        L_0x026f:
            r23 = r0
            r18 = r20
            r16 = r21
            goto L_0x0215
        L_0x0276:
            r18 = r20
            r16 = r21
            goto L_0x027f
        L_0x027b:
            r18 = r20
            r16 = r21
        L_0x027f:
            return
        L_0x0280:
            r0 = move-exception
            r18 = r20
            r16 = r21
            r1 = r11
            goto L_0x028c
        L_0x0287:
            r0 = move-exception
            r18 = r5
            r16 = r6
        L_0x028c:
            monitor-exit(r22)     // Catch:{ all -> 0x028e }
            throw r0
        L_0x028e:
            r0 = move-exception
            goto L_0x028c
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.resetAllModes(int, java.lang.String):void");
    }

    private void evalAllForegroundOpsLocked() {
        for (int uidi = this.mUidStates.size() - 1; uidi >= 0; uidi--) {
            UidState uidState = this.mUidStates.valueAt(uidi);
            if (uidState.foregroundOps != null) {
                uidState.evalForegroundOps(this.mOpModeWatchers);
            }
        }
    }

    public void startWatchingMode(int op, String packageName, IAppOpsCallback callback) {
        startWatchingModeWithFlags(op, packageName, 0, callback);
    }

    public void startWatchingModeWithFlags(int op, String packageName, int flags, IAppOpsCallback callback) {
        int i;
        int i2 = op;
        String str = packageName;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        Preconditions.checkArgumentInRange(i2, -1, 90, "Invalid op code: " + i2);
        if (callback != null) {
            synchronized (this) {
                if (i2 != -1) {
                    try {
                        i = AppOpsManager.opToSwitch(op);
                    } catch (Throwable th) {
                        th = th;
                        int i3 = i2;
                        throw th;
                    }
                } else {
                    i = i2;
                }
                int op2 = i;
                try {
                    ModeCallback cb = this.mModeWatchers.get(callback.asBinder());
                    if (cb == null) {
                        cb = new ModeCallback(callback, -1, flags, callingUid, callingPid);
                        this.mModeWatchers.put(callback.asBinder(), cb);
                    }
                    if (op2 != -1) {
                        ArraySet<ModeCallback> cbs = this.mOpModeWatchers.get(op2);
                        if (cbs == null) {
                            cbs = new ArraySet<>();
                            this.mOpModeWatchers.put(op2, cbs);
                        }
                        cbs.add(cb);
                    }
                    if (str != null) {
                        ArraySet<ModeCallback> cbs2 = this.mPackageModeWatchers.get(str);
                        if (cbs2 == null) {
                            cbs2 = new ArraySet<>();
                            this.mPackageModeWatchers.put(str, cbs2);
                        }
                        cbs2.add(cb);
                    }
                    evalAllForegroundOpsLocked();
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            }
        }
    }

    public void stopWatchingMode(IAppOpsCallback callback) {
        if (callback != null) {
            synchronized (this) {
                ModeCallback cb = this.mModeWatchers.remove(callback.asBinder());
                if (cb != null) {
                    cb.unlinkToDeath();
                    for (int i = this.mOpModeWatchers.size() - 1; i >= 0; i--) {
                        ArraySet<ModeCallback> cbs = this.mOpModeWatchers.valueAt(i);
                        cbs.remove(cb);
                        if (cbs.size() <= 0) {
                            this.mOpModeWatchers.removeAt(i);
                        }
                    }
                    for (int i2 = this.mPackageModeWatchers.size() - 1; i2 >= 0; i2--) {
                        ArraySet<ModeCallback> cbs2 = this.mPackageModeWatchers.valueAt(i2);
                        cbs2.remove(cb);
                        if (cbs2.size() <= 0) {
                            this.mPackageModeWatchers.removeAt(i2);
                        }
                    }
                }
                evalAllForegroundOpsLocked();
            }
        }
    }

    public IBinder getToken(IBinder clientToken) {
        ClientState cs;
        synchronized (this) {
            cs = this.mClients.get(clientToken);
            if (cs == null) {
                cs = new ClientState(clientToken);
                this.mClients.put(clientToken, cs);
            }
        }
        return cs;
    }

    public AppOpsManagerInternal.CheckOpsDelegate getAppOpsServiceDelegate() {
        AppOpsManagerInternal.CheckOpsDelegate checkOpsDelegate;
        synchronized (this) {
            checkOpsDelegate = this.mCheckOpsDelegate;
        }
        return checkOpsDelegate;
    }

    public void setAppOpsServiceDelegate(AppOpsManagerInternal.CheckOpsDelegate delegate) {
        synchronized (this) {
            this.mCheckOpsDelegate = delegate;
        }
    }

    public int checkOperationRaw(int code, int uid, String packageName) {
        return checkOperationInternal(code, uid, packageName, true);
    }

    public int checkOperation(int code, int uid, String packageName) {
        return checkOperationInternal(code, uid, packageName, false);
    }

    private int checkOperationInternal(int code, int uid, String packageName, boolean raw) {
        AppOpsManagerInternal.CheckOpsDelegate delegate;
        synchronized (this) {
            delegate = this.mCheckOpsDelegate;
        }
        if (delegate == null) {
            return checkOperationImpl(code, uid, packageName, raw);
        }
        return delegate.checkOperation(code, uid, packageName, raw, new QuadFunction() {
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                return Integer.valueOf(AppOpsService.this.checkOperationImpl(((Integer) obj).intValue(), ((Integer) obj2).intValue(), (String) obj3, ((Boolean) obj4).booleanValue()));
            }
        });
    }

    /* access modifiers changed from: private */
    public int checkOperationImpl(int code, int uid, String packageName, boolean raw) {
        verifyIncomingOp(code);
        String resolvedPackageName = resolvePackageName(uid, packageName);
        if (resolvedPackageName == null) {
            return 1;
        }
        AppOpsServiceState appOpsServiceState = this.mServiceState;
        if (AppOpsServiceState.isCtsIgnore(resolvedPackageName) || !shouldSkipOpCheck(resolvedPackageName, code) || (this.mServiceState.isAppPermissionControlOpen(code, uid) && !this.mServiceState.isMiuiAllowed(code, uid, resolvedPackageName))) {
            return checkOperationUnchecked(code, uid, resolvedPackageName, raw);
        }
        return this.mServiceState.allowedToMode(code, uid, resolvedPackageName);
    }

    private boolean shouldSkipOpCheck(String packageName, int code) {
        return !Build.IS_INTERNATIONAL_BUILD || code > 10000 || code == 24 || code == 23 || code == 25 || code == 15 || code == 22 || this.mDefaultGrantList.contains(packageName);
    }

    private int checkOperationUnchecked(int code, int uid, String packageName, boolean raw) {
        return checkOperationUnchecked(code, uid, packageName, raw, true);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003d, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x005c, code lost:
        return r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int checkOperationUnchecked(int r9, int r10, java.lang.String r11, boolean r12, boolean r13) {
        /*
            r8 = this;
            boolean r0 = r8.verifyAndGetIsPrivileged(r10, r11)     // Catch:{ Exception -> 0x0060 }
            boolean r1 = r8.isOpRestrictedDueToSuspend(r9, r11, r10)
            r2 = 1
            if (r1 == 0) goto L_0x000d
            return r2
        L_0x000d:
            monitor-enter(r8)
            boolean r1 = r8.isOpRestrictedLocked(r10, r9, r11, r0)     // Catch:{ all -> 0x005d }
            if (r1 == 0) goto L_0x0016
            monitor-exit(r8)     // Catch:{ all -> 0x005d }
            return r2
        L_0x0016:
            int r1 = android.app.AppOpsManager.opToSwitch(r9)     // Catch:{ all -> 0x005d }
            r9 = r1
            r1 = 0
            com.android.server.appop.AppOpsService$UidState r1 = r8.getUidStateLocked(r10, r1)     // Catch:{ all -> 0x005d }
            if (r1 == 0) goto L_0x003e
            android.util.SparseIntArray r2 = r1.opModes     // Catch:{ all -> 0x005d }
            if (r2 == 0) goto L_0x003e
            android.util.SparseIntArray r2 = r1.opModes     // Catch:{ all -> 0x005d }
            int r2 = r2.indexOfKey(r9)     // Catch:{ all -> 0x005d }
            if (r2 < 0) goto L_0x003e
            android.util.SparseIntArray r2 = r1.opModes     // Catch:{ all -> 0x005d }
            int r2 = r2.get(r9)     // Catch:{ all -> 0x005d }
            if (r12 == 0) goto L_0x0038
            r3 = r2
            goto L_0x003c
        L_0x0038:
            int r3 = r1.evalMode(r9, r2)     // Catch:{ all -> 0x005d }
        L_0x003c:
            monitor-exit(r8)     // Catch:{ all -> 0x005d }
            return r3
        L_0x003e:
            r6 = 0
            r7 = 0
            r2 = r8
            r3 = r9
            r4 = r10
            r5 = r11
            com.android.server.appop.AppOpsService$Op r2 = r2.getOpLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x005d }
            if (r2 != 0) goto L_0x0050
            int r3 = android.app.AppOpsManager.opToDefaultMode(r9)     // Catch:{ all -> 0x005d }
            monitor-exit(r8)     // Catch:{ all -> 0x005d }
            return r3
        L_0x0050:
            if (r12 == 0) goto L_0x0057
            int r3 = r2.mode     // Catch:{ all -> 0x005d }
            goto L_0x005b
        L_0x0057:
            int r3 = r2.evalMode()     // Catch:{ all -> 0x005d }
        L_0x005b:
            monitor-exit(r8)     // Catch:{ all -> 0x005d }
            return r3
        L_0x005d:
            r1 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x005d }
            throw r1
        L_0x0060:
            r0 = move-exception
            java.lang.String r1 = "AppOps"
            java.lang.String r2 = "checkOperation"
            android.util.Slog.e(r1, r2, r0)
            int r1 = android.app.AppOpsManager.opToDefaultMode(r9)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.checkOperationUnchecked(int, int, java.lang.String, boolean, boolean):int");
    }

    public int checkAudioOperation(int code, int usage, int uid, String packageName) {
        AppOpsManagerInternal.CheckOpsDelegate delegate;
        synchronized (this) {
            delegate = this.mCheckOpsDelegate;
        }
        if (delegate == null) {
            return checkAudioOperationImpl(code, usage, uid, packageName);
        }
        return delegate.checkAudioOperation(code, usage, uid, packageName, new QuadFunction() {
            public final Object apply(Object obj, Object obj2, Object obj3, Object obj4) {
                return Integer.valueOf(AppOpsService.this.checkAudioOperationImpl(((Integer) obj).intValue(), ((Integer) obj2).intValue(), ((Integer) obj3).intValue(), (String) obj4));
            }
        });
    }

    /* access modifiers changed from: private */
    public int checkAudioOperationImpl(int code, int usage, int uid, String packageName) {
        boolean suspended;
        try {
            suspended = isPackageSuspendedForUser(packageName, uid);
        } catch (IllegalArgumentException e) {
            suspended = false;
        }
        if (packageName == null) {
            packageName = resolvePackageName(uid, packageName);
        }
        if (suspended) {
            Slog.i(TAG, "Audio disabled for suspended package=" + packageName + " for uid=" + uid);
            return 1;
        }
        synchronized (this) {
            int mode = checkRestrictionLocked(code, usage, uid, packageName);
            if (mode != 0) {
                return mode;
            }
            return checkOperation(code, uid, packageName);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private boolean isPackageSuspendedForUser(String pkg, int uid) {
        long identity = Binder.clearCallingIdentity();
        try {
            boolean isPackageSuspendedForUser = AppGlobals.getPackageManager().isPackageSuspendedForUser(pkg, UserHandle.getUserId(uid));
            Binder.restoreCallingIdentity(identity);
            return isPackageSuspendedForUser;
        } catch (RemoteException e) {
            throw new SecurityException("Could not talk to package manager service");
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(identity);
            throw th;
        }
    }

    private int checkRestrictionLocked(int code, int usage, int uid, String packageName) {
        Restriction r;
        SparseArray<Restriction> usageRestrictions = this.mAudioRestrictions.get(code);
        if (usageRestrictions == null || (r = usageRestrictions.get(usage)) == null || r.exceptionPackages.contains(packageName)) {
            return 0;
        }
        return r.mode;
    }

    public void setAudioRestriction(int code, int usage, int uid, int mode, String[] exceptionPackages) {
        enforceManageAppOpsModes(Binder.getCallingPid(), Binder.getCallingUid(), uid);
        verifyIncomingUid(uid);
        verifyIncomingOp(code);
        synchronized (this) {
            SparseArray<Restriction> usageRestrictions = this.mAudioRestrictions.get(code);
            if (usageRestrictions == null) {
                usageRestrictions = new SparseArray<>();
                this.mAudioRestrictions.put(code, usageRestrictions);
            }
            usageRestrictions.remove(usage);
            if (mode != 0) {
                Restriction r = new Restriction();
                r.mode = mode;
                if (exceptionPackages != null) {
                    r.exceptionPackages = new ArraySet<>(N);
                    for (String pkg : exceptionPackages) {
                        if (pkg != null) {
                            r.exceptionPackages.add(pkg.trim());
                        }
                    }
                }
                usageRestrictions.put(usage, r);
            }
        }
        this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppOpsService$GUeKjlbzT65s86vaxy5gvOajuhw.INSTANCE, this, Integer.valueOf(code), -2));
    }

    public int checkPackage(int uid, String packageName) {
        Preconditions.checkNotNull(packageName);
        try {
            verifyAndGetIsPrivileged(uid, packageName);
            return 0;
        } catch (SecurityException e) {
            return 2;
        }
    }

    public int noteProxyOperation(int code, int proxyUid, String proxyPackageName, int proxiedUid, String proxiedPackageName) {
        int proxyFlags;
        int proxiedFlags;
        int i = proxyUid;
        verifyIncomingUid(i);
        verifyIncomingOp(code);
        String resolveProxyPackageName = resolvePackageName(proxyUid, proxyPackageName);
        if (resolveProxyPackageName == null) {
            return 1;
        }
        boolean isProxyTrusted = this.mContext.checkPermission("android.permission.UPDATE_APP_OPS_STATS", -1, i) == 0;
        if (isProxyTrusted) {
            proxyFlags = 2;
        } else {
            proxyFlags = 4;
        }
        int proxyMode = noteOperationUnchecked(code, proxyUid, resolveProxyPackageName, -1, (String) null, proxyFlags);
        if (proxyMode != 0) {
            int i2 = proxiedUid;
        } else if (Binder.getCallingUid() != proxiedUid) {
            String resolveProxiedPackageName = resolvePackageName(proxiedUid, proxiedPackageName);
            if (resolveProxiedPackageName == null) {
                return 1;
            }
            if (isProxyTrusted) {
                proxiedFlags = 8;
            } else {
                proxiedFlags = 16;
            }
            return noteOperationUnchecked(code, proxiedUid, resolveProxiedPackageName, proxyUid, resolveProxyPackageName, proxiedFlags);
        }
        return proxyMode;
    }

    public int noteOperation(int code, int uid, String packageName) {
        AppOpsManagerInternal.CheckOpsDelegate delegate;
        synchronized (this) {
            delegate = this.mCheckOpsDelegate;
        }
        if (delegate == null) {
            return noteOperationImpl(code, uid, packageName);
        }
        return delegate.noteOperation(code, uid, packageName, new TriFunction() {
            public final Object apply(Object obj, Object obj2, Object obj3) {
                return Integer.valueOf(AppOpsService.this.noteOperationImpl(((Integer) obj).intValue(), ((Integer) obj2).intValue(), (String) obj3));
            }
        });
    }

    /* access modifiers changed from: private */
    public int noteOperationImpl(int code, int uid, String packageName) {
        verifyIncomingUid(uid);
        verifyIncomingOp(code);
        String resolvedPackageName = resolvePackageName(uid, packageName);
        if (resolvedPackageName == null) {
            return 1;
        }
        return noteOperationUnchecked(code, uid, resolvedPackageName, -1, (String) null, 1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0226, code lost:
        monitor-exit(r30);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x0227, code lost:
        r16 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x0229, code lost:
        if (r16 != 0) goto L_0x0273;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x022f, code lost:
        if (android.app.AppOpsManager.isSupportVirtualGrant(r31) == false) goto L_0x0273;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x0231, code lost:
        r9 = android.app.AppOpsManager.convertVirtualOp(r31);
        r17 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x023d, code lost:
        r16 = checkOperation(r9, r11, r12);
        android.os.Binder.restoreCallingIdentity(r17);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x0243, code lost:
        if (r16 == 0) goto L_0x0246;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x0245, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x0246, code lost:
        r2 = r1.mServiceState;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0248, code lost:
        if (r0 == false) goto L_0x024c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x024a, code lost:
        r5 = r9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x024c, code lost:
        r5 = r10;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x024d, code lost:
        if (r0 == false) goto L_0x0252;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x024f, code lost:
        r7 = 4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x0252, code lost:
        r7 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x0253, code lost:
        r21 = r8;
        r19 = r9;
        r2.onAppApplyOperation(r32, r33, r5, r16, r7, r15.state, r36);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x0269, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x026a, code lost:
        r21 = r8;
        r19 = r9;
        r2 = r0;
        android.os.Binder.restoreCallingIdentity(r17);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0272, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0273, code lost:
        r21 = r8;
        r1.mServiceState.onAppApplyOperation(r32, r33, r31, r16, 1, r15.state, r36);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x0287, code lost:
        return r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x0288, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x0289, code lost:
        r21 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x028c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:?, code lost:
        monitor-exit(r30);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x028e, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x01ad, code lost:
        if (r1.mServiceState.isAppPermissionControlOpen(r10, r11) != false) goto L_0x01bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x01af, code lost:
        r2 = r1.mServiceState;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01b5, code lost:
        if (com.android.server.AppOpsServiceState.isCtsIgnore(r33) != false) goto L_0x01d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x01b7, code lost:
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x01c0, code lost:
        if (shouldSkipOpCheck(r12, r10) == false) goto L_0x01d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x01c8, code lost:
        if (r1.mServiceState.isMiuiAllowed(r10, r11, r12) == false) goto L_0x01d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01ca, code lost:
        r2 = r1.mServiceState.allowedToMode(r10, r11, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x01d0, code lost:
        if (r14 == null) goto L_0x01d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01d2, code lost:
        com.android.server.appop.AppOpsService.Op.access$102(r14, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x01d6, code lost:
        r2 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x01d9, code lost:
        if (r2 != 5) goto L_0x01e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:88:0x01db, code lost:
        r9 = r1.mServiceState.askOperationLocked(r13, r11, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x01e3, code lost:
        r9 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01e5, code lost:
        if (r9 == 0) goto L_0x01fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x01e7, code lost:
        r0 = false;
        r21.rejected(java.lang.System.currentTimeMillis(), r34, r35, r15.state, r36);
        r16 = r9;
        r8 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x01fe, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01ff, code lost:
        monitor-enter(r30);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:?, code lost:
        r21.accessed(java.lang.System.currentTimeMillis(), r34, r35, r15.state, r36);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0213, code lost:
        r8 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:?, code lost:
        r1.mHistoricalRegistry.incrementOpAccessedCount(r8.op, r32, r33, r15.state, r36);
        scheduleOpNotedIfNeededLocked(r10, r11, r12, 0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int noteOperationUnchecked(int r31, int r32, java.lang.String r33, int r34, java.lang.String r35, int r36) {
        /*
            r30 = this;
            r1 = r30
            r10 = r31
            r11 = r32
            r12 = r33
            r13 = r36
            r14 = 2
            boolean r0 = r1.verifyAndGetIsPrivileged(r11, r12)     // Catch:{ SecurityException -> 0x02b5 }
            r15 = r0
            r16 = 0
            r17 = 0
            r18 = 0
            r19 = 0
            monitor-enter(r30)
            r0 = 1
            com.android.server.appop.AppOpsService$Ops r2 = r1.getOpsRawLocked(r11, r12, r15, r0)     // Catch:{ all -> 0x02ae }
            r9 = r2
            if (r9 != 0) goto L_0x003d
            r1.scheduleOpNotedIfNeededLocked(r10, r11, r12, r0)     // Catch:{ all -> 0x0038 }
            com.android.server.AppOpsServiceState r2 = r1.mServiceState     // Catch:{ all -> 0x0038 }
            r6 = 2
            r7 = 1
            r8 = 0
            r3 = r32
            r4 = r33
            r5 = r31
            r0 = r9
            r9 = r36
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0038 }
            monitor-exit(r30)     // Catch:{ all -> 0x0038 }
            return r14
        L_0x0038:
            r0 = move-exception
            r20 = r15
            goto L_0x02b1
        L_0x003d:
            r14 = r9
            com.android.server.appop.AppOpsService$Op r2 = r1.getOpLocked(r14, r10, r0)     // Catch:{ all -> 0x02ae }
            r9 = r2
            boolean r2 = r1.isOpRestrictedLocked(r11, r10, r12, r15)     // Catch:{ all -> 0x02a6 }
            if (r2 == 0) goto L_0x006e
            r1.scheduleOpNotedIfNeededLocked(r10, r11, r12, r0)     // Catch:{ all -> 0x0066 }
            com.android.server.AppOpsServiceState r2 = r1.mServiceState     // Catch:{ all -> 0x0066 }
            r6 = 1
            r7 = 1
            r8 = 0
            r3 = r32
            r4 = r33
            r5 = r31
            r20 = r15
            r15 = r9
            r9 = r36
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0061 }
            monitor-exit(r30)     // Catch:{ all -> 0x0061 }
            return r0
        L_0x0061:
            r0 = move-exception
            r16 = r15
            goto L_0x02b1
        L_0x0066:
            r0 = move-exception
            r20 = r15
            r15 = r9
            r16 = r15
            goto L_0x02b1
        L_0x006e:
            r20 = r15
            r15 = r9
            com.android.server.appop.AppOpsService$UidState r2 = r14.uidState     // Catch:{ all -> 0x02a0 }
            r9 = r2
            boolean r2 = r15.running     // Catch:{ all -> 0x0297 }
            if (r2 == 0) goto L_0x00ed
            android.app.AppOpsManager$OpEntry r2 = new android.app.AppOpsManager$OpEntry     // Catch:{ all -> 0x00e6 }
            int r3 = r15.op     // Catch:{ all -> 0x00e6 }
            boolean r4 = r15.running     // Catch:{ all -> 0x00e6 }
            int r24 = r15.mode     // Catch:{ all -> 0x00e6 }
            android.util.LongSparseLongArray r25 = r15.mAccessTimes     // Catch:{ all -> 0x00e6 }
            android.util.LongSparseLongArray r26 = r15.mRejectTimes     // Catch:{ all -> 0x00e6 }
            android.util.LongSparseLongArray r27 = r15.mDurations     // Catch:{ all -> 0x00e6 }
            android.util.LongSparseLongArray r28 = r15.mProxyUids     // Catch:{ all -> 0x00e6 }
            android.util.LongSparseArray r29 = r15.mProxyPackageNames     // Catch:{ all -> 0x00e6 }
            r21 = r2
            r22 = r3
            r23 = r4
            r21.<init>(r22, r23, r24, r25, r26, r27, r28, r29)     // Catch:{ all -> 0x00e6 }
            java.lang.String r3 = "AppOps"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00e6 }
            r4.<init>()     // Catch:{ all -> 0x00e6 }
            java.lang.String r5 = "Noting op not finished: uid "
            r4.append(r5)     // Catch:{ all -> 0x00e6 }
            r4.append(r11)     // Catch:{ all -> 0x00e6 }
            java.lang.String r5 = " pkg "
            r4.append(r5)     // Catch:{ all -> 0x00e6 }
            r4.append(r12)     // Catch:{ all -> 0x00e6 }
            java.lang.String r5 = " code "
            r4.append(r5)     // Catch:{ all -> 0x00e6 }
            r4.append(r10)     // Catch:{ all -> 0x00e6 }
            java.lang.String r5 = " time="
            r4.append(r5)     // Catch:{ all -> 0x00e6 }
            int r5 = r9.state     // Catch:{ all -> 0x00e6 }
            int r6 = r9.state     // Catch:{ all -> 0x00e6 }
            long r5 = r2.getLastAccessTime(r5, r6, r13)     // Catch:{ all -> 0x00e6 }
            r4.append(r5)     // Catch:{ all -> 0x00e6 }
            java.lang.String r5 = " duration="
            r4.append(r5)     // Catch:{ all -> 0x00e6 }
            int r5 = r9.state     // Catch:{ all -> 0x00e6 }
            int r6 = r9.state     // Catch:{ all -> 0x00e6 }
            long r5 = r2.getLastDuration(r5, r6, r13)     // Catch:{ all -> 0x00e6 }
            r4.append(r5)     // Catch:{ all -> 0x00e6 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00e6 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00e6 }
            goto L_0x00ed
        L_0x00e6:
            r0 = move-exception
            r19 = r9
            r16 = r15
            goto L_0x02b1
        L_0x00ed:
            int r2 = android.app.AppOpsManager.opToSwitch(r31)     // Catch:{ all -> 0x0297 }
            r8 = r2
            android.util.SparseIntArray r2 = r9.opModes     // Catch:{ all -> 0x0297 }
            if (r2 == 0) goto L_0x015d
            android.util.SparseIntArray r2 = r9.opModes     // Catch:{ all -> 0x0297 }
            int r2 = r2.indexOfKey(r8)     // Catch:{ all -> 0x0297 }
            if (r2 < 0) goto L_0x015d
            android.util.SparseIntArray r2 = r9.opModes     // Catch:{ all -> 0x0297 }
            int r2 = r2.get(r8)     // Catch:{ all -> 0x0297 }
            int r2 = r9.evalMode(r10, r2)     // Catch:{ all -> 0x0297 }
            r7 = r2
            if (r7 == 0) goto L_0x0152
            java.lang.String r2 = "com.android.mms"
            boolean r2 = r2.equals(r12)     // Catch:{ all -> 0x0297 }
            if (r2 != 0) goto L_0x0152
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0297 }
            int r0 = r9.state     // Catch:{ all -> 0x0297 }
            r2 = r15
            r5 = r34
            r6 = r35
            r16 = r7
            r7 = r0
            r13 = r8
            r8 = r36
            r2.rejected(r3, r5, r6, r7, r8)     // Catch:{ all -> 0x0297 }
            com.android.server.AppOpsServiceState r2 = r1.mServiceState     // Catch:{ all -> 0x0297 }
            r7 = 1
            int r8 = r9.state     // Catch:{ all -> 0x0297 }
            r3 = r32
            r4 = r33
            r5 = r31
            r6 = r16
            r21 = r15
            r15 = r9
            r9 = r36
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x0168 }
            com.android.server.appop.HistoricalRegistry r2 = r1.mHistoricalRegistry     // Catch:{ all -> 0x0168 }
            int r6 = r15.state     // Catch:{ all -> 0x0168 }
            r3 = r31
            r4 = r32
            r5 = r33
            r7 = r36
            r2.incrementOpRejected(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0168 }
            r2 = r16
            r1.scheduleOpNotedIfNeededLocked(r10, r11, r12, r2)     // Catch:{ all -> 0x0168 }
            monitor-exit(r30)     // Catch:{ all -> 0x0168 }
            return r2
        L_0x0152:
            r2 = r7
            r13 = r8
            r21 = r15
            r15 = r9
            r18 = r2
            r14 = r17
            goto L_0x01a6
        L_0x015d:
            r13 = r8
            r21 = r15
            r15 = r9
            if (r13 == r10) goto L_0x016f
            com.android.server.appop.AppOpsService$Op r9 = r1.getOpLocked(r14, r13, r0)     // Catch:{ all -> 0x0168 }
            goto L_0x0171
        L_0x0168:
            r0 = move-exception
            r19 = r15
            r16 = r21
            goto L_0x02b1
        L_0x016f:
            r9 = r21
        L_0x0171:
            r17 = r9
            int r2 = r17.evalMode()     // Catch:{ all -> 0x0168 }
            r9 = r2
            int r2 = r17.mode     // Catch:{ all -> 0x0168 }
            if (r2 == 0) goto L_0x01a1
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0168 }
            int r7 = r15.state     // Catch:{ all -> 0x0168 }
            r2 = r21
            r5 = r34
            r6 = r35
            r8 = r36
            r2.rejected(r3, r5, r6, r7, r8)     // Catch:{ all -> 0x0168 }
            com.android.server.appop.HistoricalRegistry r2 = r1.mHistoricalRegistry     // Catch:{ all -> 0x0168 }
            int r6 = r15.state     // Catch:{ all -> 0x0168 }
            r3 = r31
            r4 = r32
            r5 = r33
            r7 = r36
            r2.incrementOpRejected(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0168 }
            r1.scheduleOpNotedIfNeededLocked(r10, r11, r12, r9)     // Catch:{ all -> 0x0168 }
        L_0x01a1:
            r2 = r9
            r18 = r2
            r14 = r17
        L_0x01a6:
            monitor-exit(r30)     // Catch:{ all -> 0x028f }
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            boolean r2 = r2.isAppPermissionControlOpen(r10, r11)
            if (r2 != 0) goto L_0x01bc
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            boolean r2 = com.android.server.AppOpsServiceState.isCtsIgnore(r33)
            if (r2 != 0) goto L_0x01d6
            r18 = 0
            r2 = r18
            goto L_0x01d8
        L_0x01bc:
            boolean r2 = r1.shouldSkipOpCheck(r12, r10)
            if (r2 == 0) goto L_0x01d6
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            boolean r2 = r2.isMiuiAllowed(r10, r11, r12)
            if (r2 == 0) goto L_0x01d6
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            int r2 = r2.allowedToMode(r10, r11, r12)
            if (r14 == 0) goto L_0x01d8
            int unused = r14.mode = r2
            goto L_0x01d8
        L_0x01d6:
            r2 = r18
        L_0x01d8:
            r3 = 5
            if (r2 != r3) goto L_0x01e3
            com.android.server.AppOpsServiceState r3 = r1.mServiceState
            int r2 = r3.askOperationLocked(r13, r11, r12)
            r9 = r2
            goto L_0x01e4
        L_0x01e3:
            r9 = r2
        L_0x01e4:
            r8 = 0
            if (r9 == 0) goto L_0x01fe
            long r3 = java.lang.System.currentTimeMillis()
            int r7 = r15.state
            r2 = r21
            r5 = r34
            r6 = r35
            r0 = r8
            r8 = r36
            r2.rejected(r3, r5, r6, r7, r8)
            r16 = r9
            r8 = r21
            goto L_0x0229
        L_0x01fe:
            r0 = r8
            monitor-enter(r30)
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x028c }
            int r7 = r15.state     // Catch:{ all -> 0x028c }
            r2 = r21
            r5 = r34
            r6 = r35
            r8 = r36
            r2.accessed(r3, r5, r6, r7, r8)     // Catch:{ all -> 0x028c }
            com.android.server.appop.HistoricalRegistry r2 = r1.mHistoricalRegistry     // Catch:{ all -> 0x028c }
            r8 = r21
            int r3 = r8.op     // Catch:{ all -> 0x0288 }
            int r6 = r15.state     // Catch:{ all -> 0x0288 }
            r4 = r32
            r5 = r33
            r7 = r36
            r2.incrementOpAccessedCount(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0288 }
            r1.scheduleOpNotedIfNeededLocked(r10, r11, r12, r0)     // Catch:{ all -> 0x0288 }
            r9 = 0
            monitor-exit(r30)     // Catch:{ all -> 0x0288 }
            r16 = r9
        L_0x0229:
            if (r16 != 0) goto L_0x0273
            boolean r2 = android.app.AppOpsManager.isSupportVirtualGrant(r31)
            if (r2 == 0) goto L_0x0273
            int r9 = android.app.AppOpsManager.convertVirtualOp(r31)
            long r17 = android.os.Binder.clearCallingIdentity()
            int r2 = r1.checkOperation(r9, r11, r12)     // Catch:{ all -> 0x0269 }
            r16 = r2
            android.os.Binder.restoreCallingIdentity(r17)
            if (r16 == 0) goto L_0x0246
            r0 = 1
        L_0x0246:
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            if (r0 == 0) goto L_0x024c
            r5 = r9
            goto L_0x024d
        L_0x024c:
            r5 = r10
        L_0x024d:
            if (r0 == 0) goto L_0x0252
            r3 = 4
            r7 = r3
            goto L_0x0253
        L_0x0252:
            r7 = 1
        L_0x0253:
            int r6 = r15.state
            r3 = r32
            r4 = r33
            r19 = r6
            r6 = r16
            r21 = r8
            r8 = r19
            r19 = r9
            r9 = r36
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8, r9)
            goto L_0x0287
        L_0x0269:
            r0 = move-exception
            r21 = r8
            r19 = r9
            r2 = r0
            android.os.Binder.restoreCallingIdentity(r17)
            throw r2
        L_0x0273:
            r21 = r8
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            r7 = 1
            int r8 = r15.state
            r3 = r32
            r4 = r33
            r5 = r31
            r6 = r16
            r9 = r36
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8, r9)
        L_0x0287:
            return r16
        L_0x0288:
            r0 = move-exception
            r21 = r8
            goto L_0x028d
        L_0x028c:
            r0 = move-exception
        L_0x028d:
            monitor-exit(r30)     // Catch:{ all -> 0x028c }
            throw r0
        L_0x028f:
            r0 = move-exception
            r17 = r14
            r19 = r15
            r16 = r21
            goto L_0x02b1
        L_0x0297:
            r0 = move-exception
            r21 = r15
            r15 = r9
            r19 = r15
            r16 = r21
            goto L_0x02b1
        L_0x02a0:
            r0 = move-exception
            r21 = r15
            r16 = r21
            goto L_0x02b1
        L_0x02a6:
            r0 = move-exception
            r21 = r9
            r20 = r15
            r16 = r21
            goto L_0x02b1
        L_0x02ae:
            r0 = move-exception
            r20 = r15
        L_0x02b1:
            monitor-exit(r30)     // Catch:{ all -> 0x02b3 }
            throw r0
        L_0x02b3:
            r0 = move-exception
            goto L_0x02b1
        L_0x02b5:
            r0 = move-exception
            r2 = r0
            r0 = r2
            java.lang.String r2 = "AppOps"
            java.lang.String r3 = "noteOperation"
            android.util.Slog.e(r2, r3, r0)
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.noteOperationUnchecked(int, int, java.lang.String, int, java.lang.String, int):int");
    }

    public void startWatchingActive(int[] ops, IAppOpsActiveCallback callback) {
        SparseArray<ActiveCallback> callbacks;
        int watchedUid = -1;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WATCH_APPOPS") != 0) {
            watchedUid = callingUid;
        }
        if (ops != null) {
            Preconditions.checkArrayElementsInRange(ops, 0, 90, "Invalid op code in: " + Arrays.toString(ops));
        }
        if (callback != null) {
            synchronized (this) {
                SparseArray<ActiveCallback> callbacks2 = this.mActiveWatchers.get(callback.asBinder());
                if (callbacks2 == null) {
                    SparseArray<ActiveCallback> callbacks3 = new SparseArray<>();
                    this.mActiveWatchers.put(callback.asBinder(), callbacks3);
                    callbacks = callbacks3;
                } else {
                    callbacks = callbacks2;
                }
                ActiveCallback activeCallback = new ActiveCallback(callback, watchedUid, callingUid, callingPid);
                for (int op : ops) {
                    callbacks.put(op, activeCallback);
                }
            }
        }
    }

    public void stopWatchingActive(IAppOpsActiveCallback callback) {
        if (callback != null) {
            synchronized (this) {
                SparseArray<ActiveCallback> activeCallbacks = this.mActiveWatchers.remove(callback.asBinder());
                if (activeCallbacks != null) {
                    int callbackCount = activeCallbacks.size();
                    for (int i = 0; i < callbackCount; i++) {
                        activeCallbacks.valueAt(i).destroy();
                    }
                }
            }
        }
    }

    public void startWatchingNoted(int[] ops, IAppOpsNotedCallback callback) {
        SparseArray<NotedCallback> callbacks;
        int watchedUid = -1;
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WATCH_APPOPS") != 0) {
            watchedUid = callingUid;
        }
        Preconditions.checkArgument(!ArrayUtils.isEmpty(ops), "Ops cannot be null or empty");
        Preconditions.checkArrayElementsInRange(ops, 0, 90, "Invalid op code in: " + Arrays.toString(ops));
        Preconditions.checkNotNull(callback, "Callback cannot be null");
        synchronized (this) {
            SparseArray<NotedCallback> callbacks2 = this.mNotedWatchers.get(callback.asBinder());
            if (callbacks2 == null) {
                SparseArray<NotedCallback> callbacks3 = new SparseArray<>();
                this.mNotedWatchers.put(callback.asBinder(), callbacks3);
                callbacks = callbacks3;
            } else {
                callbacks = callbacks2;
            }
            NotedCallback notedCallback = new NotedCallback(callback, watchedUid, callingUid, callingPid);
            for (int op : ops) {
                callbacks.put(op, notedCallback);
            }
        }
    }

    public void stopWatchingNoted(IAppOpsNotedCallback callback) {
        Preconditions.checkNotNull(callback, "Callback cannot be null");
        synchronized (this) {
            SparseArray<NotedCallback> notedCallbacks = this.mNotedWatchers.remove(callback.asBinder());
            if (notedCallbacks != null) {
                int callbackCount = notedCallbacks.size();
                for (int i = 0; i < callbackCount; i++) {
                    notedCallbacks.valueAt(i).destroy();
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:100:0x01d0, code lost:
        if (r1.mServiceState.isAppPermissionControlOpen(r9, r14) != false) goto L_0x01e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x01d2, code lost:
        r2 = r1.mServiceState;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x01d8, code lost:
        if (com.android.server.AppOpsServiceState.isCtsIgnore(r27) != false) goto L_0x01e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x01da, code lost:
        r2 = 0;
        r7 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x01e1, code lost:
        r7 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x01e4, code lost:
        r7 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x01ec, code lost:
        if (r1.mServiceState.isMiuiAllowed(r9, r14, r7) == false) goto L_0x01fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x01ee, code lost:
        r2 = r1.mServiceState.allowedToMode(r9, r14, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x01f4, code lost:
        if (r12 == null) goto L_0x01fc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x01f6, code lost:
        com.android.server.appop.AppOpsService.Op.access$102(r12, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x01fa, code lost:
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x01fd, code lost:
        if (r2 != 5) goto L_0x0208;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x01ff, code lost:
        r16 = r1.mServiceState.askOperationLocked(r10, r14, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x0208, code lost:
        r16 = r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x020a, code lost:
        if (r16 == 0) goto L_0x0224;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x020c, code lost:
        r8.rejected(java.lang.System.currentTimeMillis(), -1, (java.lang.String) null, r15.state, 1);
        r19 = r7;
        r7 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x0224, code lost:
        monitor-enter(r28);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x0227, code lost:
        if (r8.startNesting != 0) goto L_0x025c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:?, code lost:
        r8.startRealtime = android.os.SystemClock.elapsedRealtime();
        r8.started(java.lang.System.currentTimeMillis(), r15.state, 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x023c, code lost:
        r19 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:?, code lost:
        r1.mHistoricalRegistry.incrementOpAccessedCount(r11, r31, r32, r15.state, 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:?, code lost:
        scheduleOpActiveChangedIfNeededLocked(r9, r14, r32, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x0250, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0251, code lost:
        r7 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0254, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x0255, code lost:
        r19 = r7;
        r7 = r32;
        r18 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x025c, code lost:
        r19 = r7;
        r7 = r32;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:?, code lost:
        r8.startNesting++;
        r15.startNesting++;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x026c, code lost:
        if (r13.mStartedOps == null) goto L_0x0278;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:?, code lost:
        r13.mStartedOps.add(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:139:0x0274, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x0275, code lost:
        r18 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:?, code lost:
        monitor-exit(r28);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0279, code lost:
        r18 = r8;
        r1.mServiceState.onAppApplyOperation(r31, r19, r30, r16, 2, r15.state);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0290, code lost:
        return r16;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:145:0x0291, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x0292, code lost:
        r18 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x0295, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0296, code lost:
        r19 = r7;
        r18 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:?, code lost:
        monitor-exit(r28);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x029b, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x029c, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x01c8, code lost:
        r14 = r31;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int startOperation(android.os.IBinder r29, int r30, int r31, java.lang.String r32, boolean r33) {
        /*
            r28 = this;
            r1 = r28
            r9 = r30
            r15 = r31
            r14 = r32
            r1.verifyIncomingUid(r15)
            r1.verifyIncomingOp(r9)
            java.lang.String r13 = resolvePackageName(r31, r32)
            r0 = 1
            if (r13 != 0) goto L_0x0016
            return r0
        L_0x0016:
            r10 = 0
            r16 = 0
            r17 = 0
            r11 = 0
            r12 = r29
            com.android.server.appop.AppOpsService$ClientState r12 = (com.android.server.appop.AppOpsService.ClientState) r12
            r18 = 2
            boolean r2 = r1.verifyAndGetIsPrivileged(r15, r14)     // Catch:{ SecurityException -> 0x02ec }
            r8 = r2
            monitor-enter(r28)
            com.android.server.appop.AppOpsService$Ops r2 = r1.getOpsRawLocked(r15, r13, r8, r0)     // Catch:{ all -> 0x02e1 }
            r7 = r2
            if (r7 != 0) goto L_0x0058
            com.android.server.AppOpsServiceState r2 = r1.mServiceState     // Catch:{ all -> 0x004f }
            r6 = 2
            r0 = 2
            r19 = 0
            r3 = r31
            r4 = r13
            r5 = r30
            r20 = r7
            r7 = r0
            r14 = r8
            r8 = r19
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0046 }
            monitor-exit(r28)     // Catch:{ all -> 0x0046 }
            return r18
        L_0x0046:
            r0 = move-exception
            r19 = r13
            r25 = r14
            r14 = r15
        L_0x004c:
            r13 = r12
            goto L_0x02e8
        L_0x004f:
            r0 = move-exception
            r25 = r8
            r19 = r13
            r14 = r15
            r13 = r12
            goto L_0x02e8
        L_0x0058:
            r20 = r7
            r14 = r8
            r8 = r20
            com.android.server.appop.AppOpsService$Op r2 = r1.getOpLocked(r8, r9, r0)     // Catch:{ all -> 0x02d9 }
            r10 = r2
            boolean r2 = r1.isOpRestrictedLocked(r15, r9, r13, r14)     // Catch:{ all -> 0x02cf }
            if (r2 == 0) goto L_0x0083
            com.android.server.AppOpsServiceState r2 = r1.mServiceState     // Catch:{ all -> 0x007b }
            r6 = 1
            r7 = 2
            r18 = 0
            r3 = r31
            r4 = r13
            r5 = r30
            r15 = r8
            r8 = r18
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x007b }
            monitor-exit(r28)     // Catch:{ all -> 0x007b }
            return r0
        L_0x007b:
            r0 = move-exception
            r19 = r13
            r25 = r14
            r14 = r31
            goto L_0x004c
        L_0x0083:
            r15 = r8
            int r2 = android.app.AppOpsManager.opToSwitch(r30)     // Catch:{ all -> 0x02c5 }
            r8 = r2
            com.android.server.appop.AppOpsService$UidState r2 = r15.uidState     // Catch:{ all -> 0x02c5 }
            r7 = r2
            int r11 = r10.op     // Catch:{ all -> 0x02b8 }
            android.util.SparseIntArray r2 = r7.opModes     // Catch:{ all -> 0x02b8 }
            r3 = 3
            if (r2 == 0) goto L_0x0145
            android.util.SparseIntArray r2 = r7.opModes     // Catch:{ all -> 0x0135 }
            int r2 = r2.indexOfKey(r8)     // Catch:{ all -> 0x0135 }
            if (r2 < 0) goto L_0x0145
            android.util.SparseIntArray r2 = r7.opModes     // Catch:{ all -> 0x0135 }
            int r2 = r2.get(r8)     // Catch:{ all -> 0x0135 }
            int r2 = r7.evalMode(r9, r2)     // Catch:{ all -> 0x0135 }
            r6 = r2
            if (r6 == 0) goto L_0x011b
            if (r33 == 0) goto L_0x00bb
            if (r6 == r3) goto L_0x00ad
            goto L_0x00bb
        L_0x00ad:
            r4 = r31
            r5 = r32
            r9 = r8
            r8 = r10
            r26 = r12
            r6 = r13
            r25 = r14
            r2 = r15
            goto L_0x0129
        L_0x00bb:
            long r19 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0135 }
            r21 = -1
            r22 = 0
            int r0 = r7.state     // Catch:{ all -> 0x0135 }
            r24 = 1
            r18 = r10
            r23 = r0
            r18.rejected(r19, r21, r22, r23, r24)     // Catch:{ all -> 0x0135 }
            com.android.server.AppOpsServiceState r2 = r1.mServiceState     // Catch:{ all -> 0x0135 }
            r0 = 2
            int r5 = r7.state     // Catch:{ all -> 0x0135 }
            r3 = r31
            r4 = r13
            r18 = r5
            r5 = r30
            r19 = r6
            r20 = r15
            r15 = r7
            r7 = r0
            r9 = r8
            r8 = r18
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x010a }
            com.android.server.appop.HistoricalRegistry r0 = r1.mHistoricalRegistry     // Catch:{ all -> 0x010a }
            int r2 = r15.state     // Catch:{ all -> 0x010a }
            r3 = 1
            r8 = r10
            r10 = r0
            r7 = r12
            r12 = r31
            r6 = r13
            r13 = r32
            r5 = r32
            r25 = r14
            r14 = r2
            r4 = r31
            r26 = r7
            r7 = r15
            r2 = r20
            r15 = r3
            r10.incrementOpRejected(r11, r12, r13, r14, r15)     // Catch:{ all -> 0x0105 }
            monitor-exit(r28)     // Catch:{ all -> 0x0105 }
            return r19
        L_0x0105:
            r0 = move-exception
            r9 = r30
            goto L_0x015c
        L_0x010a:
            r0 = move-exception
            r5 = r32
            r8 = r10
            r25 = r14
            r7 = r15
            r9 = r30
            r14 = r31
            r11 = r7
            r19 = r13
            r13 = r12
            goto L_0x02e8
        L_0x011b:
            r4 = r31
            r5 = r32
            r19 = r6
            r9 = r8
            r8 = r10
            r26 = r12
            r6 = r13
            r25 = r14
            r2 = r15
        L_0x0129:
            r27 = r6
            r15 = r7
            r10 = r9
            r12 = r16
            r13 = r26
            r9 = r30
            goto L_0x01c7
        L_0x0135:
            r0 = move-exception
            r5 = r32
            r8 = r10
            r25 = r14
            r9 = r30
            r14 = r31
            r11 = r7
            r19 = r13
            r13 = r12
            goto L_0x02e8
        L_0x0145:
            r4 = r31
            r5 = r32
            r9 = r8
            r8 = r10
            r26 = r12
            r6 = r13
            r25 = r14
            r2 = r15
            r10 = r9
            r9 = r30
            if (r10 == r9) goto L_0x0165
            com.android.server.appop.AppOpsService$Op r12 = r1.getOpLocked(r2, r10, r0)     // Catch:{ all -> 0x015b }
            goto L_0x0166
        L_0x015b:
            r0 = move-exception
        L_0x015c:
            r14 = r4
            r19 = r6
            r11 = r7
            r10 = r8
            r13 = r26
            goto L_0x02e8
        L_0x0165:
            r12 = r8
        L_0x0166:
            r16 = r12
            int r12 = r16.evalMode()     // Catch:{ all -> 0x02ab }
            if (r12 == 0) goto L_0x01be
            if (r33 == 0) goto L_0x0179
            if (r12 == r3) goto L_0x0173
            goto L_0x0179
        L_0x0173:
            r27 = r6
            r15 = r7
            r13 = r26
            goto L_0x01c5
        L_0x0179:
            long r19 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x01b2 }
            r21 = -1
            r22 = 0
            int r3 = r7.state     // Catch:{ all -> 0x01b2 }
            r24 = 1
            r18 = r8
            r23 = r3
            r18.rejected(r19, r21, r22, r23, r24)     // Catch:{ all -> 0x01b2 }
            com.android.server.appop.HistoricalRegistry r3 = r1.mHistoricalRegistry     // Catch:{ all -> 0x01b2 }
            int r13 = r7.state     // Catch:{ all -> 0x01b2 }
            r14 = 1
            r15 = r2
            r2 = r3
            r3 = r11
            r4 = r31
            r5 = r32
            r27 = r6
            r6 = r13
            r20 = r15
            r13 = r26
            r15 = r7
            r7 = r14
            r2.incrementOpRejected(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x01a9 }
            r17 = r12
            r12 = r16
            goto L_0x01c7
        L_0x01a9:
            r0 = move-exception
            r14 = r31
            r10 = r8
            r11 = r15
            r19 = r27
            goto L_0x02e8
        L_0x01b2:
            r0 = move-exception
            r15 = r7
            r13 = r26
            r14 = r31
            r19 = r6
            r10 = r8
            r11 = r15
            goto L_0x02e8
        L_0x01be:
            r20 = r2
            r27 = r6
            r15 = r7
            r13 = r26
        L_0x01c5:
            r12 = r16
        L_0x01c7:
            monitor-exit(r28)     // Catch:{ all -> 0x029e }
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            r14 = r31
            boolean r2 = r2.isAppPermissionControlOpen(r9, r14)
            if (r2 != 0) goto L_0x01e4
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            boolean r2 = com.android.server.AppOpsServiceState.isCtsIgnore(r27)
            if (r2 != 0) goto L_0x01e1
            r17 = 0
            r2 = r17
            r7 = r27
            goto L_0x01fc
        L_0x01e1:
            r7 = r27
            goto L_0x01fa
        L_0x01e4:
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            r7 = r27
            boolean r2 = r2.isMiuiAllowed(r9, r14, r7)
            if (r2 == 0) goto L_0x01fa
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            int r2 = r2.allowedToMode(r9, r14, r7)
            if (r12 == 0) goto L_0x01fc
            int unused = r12.mode = r2
            goto L_0x01fc
        L_0x01fa:
            r2 = r17
        L_0x01fc:
            r3 = 5
            if (r2 != r3) goto L_0x0208
            com.android.server.AppOpsServiceState r3 = r1.mServiceState
            int r2 = r3.askOperationLocked(r10, r14, r7)
            r16 = r2
            goto L_0x020a
        L_0x0208:
            r16 = r2
        L_0x020a:
            if (r16 == 0) goto L_0x0224
            long r19 = java.lang.System.currentTimeMillis()
            r21 = -1
            r22 = 0
            int r0 = r15.state
            r24 = 1
            r18 = r8
            r23 = r0
            r18.rejected(r19, r21, r22, r23, r24)
            r19 = r7
            r7 = r32
            goto L_0x0279
        L_0x0224:
            monitor-enter(r28)
            int r2 = r8.startNesting     // Catch:{ all -> 0x0295 }
            if (r2 != 0) goto L_0x025c
            long r2 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0254 }
            r8.startRealtime = r2     // Catch:{ all -> 0x0254 }
            long r2 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0254 }
            int r4 = r15.state     // Catch:{ all -> 0x0254 }
            r8.started(r2, r4, r0)     // Catch:{ all -> 0x0254 }
            com.android.server.appop.HistoricalRegistry r2 = r1.mHistoricalRegistry     // Catch:{ all -> 0x0254 }
            int r6 = r15.state     // Catch:{ all -> 0x0254 }
            r17 = 1
            r3 = r11
            r4 = r31
            r5 = r32
            r19 = r7
            r7 = r17
            r2.incrementOpAccessedCount(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x0250 }
            r7 = r32
            r1.scheduleOpActiveChangedIfNeededLocked(r9, r14, r7, r0)     // Catch:{ all -> 0x0274 }
            goto L_0x0260
        L_0x0250:
            r0 = move-exception
            r7 = r32
            goto L_0x0275
        L_0x0254:
            r0 = move-exception
            r19 = r7
            r7 = r32
            r18 = r8
            goto L_0x029a
        L_0x025c:
            r19 = r7
            r7 = r32
        L_0x0260:
            int r2 = r8.startNesting     // Catch:{ all -> 0x0291 }
            int r2 = r2 + r0
            r8.startNesting = r2     // Catch:{ all -> 0x0291 }
            int r2 = r15.startNesting     // Catch:{ all -> 0x0291 }
            int r2 = r2 + r0
            r15.startNesting = r2     // Catch:{ all -> 0x0291 }
            java.util.ArrayList<com.android.server.appop.AppOpsService$Op> r0 = r13.mStartedOps     // Catch:{ all -> 0x0291 }
            if (r0 == 0) goto L_0x0278
            java.util.ArrayList<com.android.server.appop.AppOpsService$Op> r0 = r13.mStartedOps     // Catch:{ all -> 0x0274 }
            r0.add(r8)     // Catch:{ all -> 0x0274 }
            goto L_0x0278
        L_0x0274:
            r0 = move-exception
        L_0x0275:
            r18 = r8
            goto L_0x029a
        L_0x0278:
            monitor-exit(r28)     // Catch:{ all -> 0x0291 }
        L_0x0279:
            com.android.server.AppOpsServiceState r2 = r1.mServiceState
            r0 = 2
            int r6 = r15.state
            r3 = r31
            r4 = r19
            r5 = r30
            r17 = r6
            r6 = r16
            r7 = r0
            r18 = r8
            r8 = r17
            r2.onAppApplyOperation(r3, r4, r5, r6, r7, r8)
            return r16
        L_0x0291:
            r0 = move-exception
            r18 = r8
            goto L_0x029a
        L_0x0295:
            r0 = move-exception
            r19 = r7
            r18 = r8
        L_0x029a:
            monitor-exit(r28)     // Catch:{ all -> 0x029c }
            throw r0
        L_0x029c:
            r0 = move-exception
            goto L_0x029a
        L_0x029e:
            r0 = move-exception
            r14 = r31
            r18 = r8
            r19 = r27
            r16 = r12
            r11 = r15
            r10 = r18
            goto L_0x02e8
        L_0x02ab:
            r0 = move-exception
            r14 = r4
            r19 = r6
            r15 = r7
            r18 = r8
            r13 = r26
            r11 = r15
            r10 = r18
            goto L_0x02e8
        L_0x02b8:
            r0 = move-exception
            r15 = r7
            r18 = r10
            r19 = r13
            r25 = r14
            r14 = r31
            r13 = r12
            r11 = r15
            goto L_0x02e8
        L_0x02c5:
            r0 = move-exception
            r18 = r10
            r19 = r13
            r25 = r14
            r14 = r31
            goto L_0x02d7
        L_0x02cf:
            r0 = move-exception
            r18 = r10
            r19 = r13
            r25 = r14
            r14 = r15
        L_0x02d7:
            r13 = r12
            goto L_0x02e8
        L_0x02d9:
            r0 = move-exception
            r19 = r13
            r25 = r14
            r14 = r15
            r13 = r12
            goto L_0x02e8
        L_0x02e1:
            r0 = move-exception
            r25 = r8
            r19 = r13
            r14 = r15
            r13 = r12
        L_0x02e8:
            monitor-exit(r28)     // Catch:{ all -> 0x02ea }
            throw r0
        L_0x02ea:
            r0 = move-exception
            goto L_0x02e8
        L_0x02ec:
            r0 = move-exception
            r19 = r13
            r14 = r15
            r13 = r12
            r2 = r0
            r0 = r2
            java.lang.String r2 = "AppOps"
            java.lang.String r3 = "startOperation"
            android.util.Slog.e(r2, r3, r0)
            return r18
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.startOperation(android.os.IBinder, int, int, java.lang.String, boolean):int");
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0036, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0037, code lost:
        r6 = r11;
        r16 = r12;
        r2 = r13;
        r4 = r14;
        r3 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00c5, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00c6, code lost:
        android.os.Binder.restoreCallingIdentity(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00c9, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00f0, code lost:
        return;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:15:0x0034, B:27:0x004e] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void finishOperation(android.os.IBinder r18, int r19, int r20, java.lang.String r21) {
        /*
            r17 = this;
            r7 = r17
            r8 = r18
            r15 = r19
            r14 = r20
            r13 = r21
            r7.verifyIncomingUid(r14)
            r7.verifyIncomingOp(r15)
            java.lang.String r12 = resolvePackageName(r20, r21)
            if (r12 != 0) goto L_0x0017
            return
        L_0x0017:
            boolean r0 = r8 instanceof com.android.server.appop.AppOpsService.ClientState
            if (r0 != 0) goto L_0x001c
            return
        L_0x001c:
            r11 = r8
            com.android.server.appop.AppOpsService$ClientState r11 = (com.android.server.appop.AppOpsService.ClientState) r11
            boolean r5 = r7.verifyAndGetIsPrivileged(r14, r13)     // Catch:{ SecurityException -> 0x0100 }
            monitor-enter(r17)
            r6 = 1
            r1 = r17
            r2 = r19
            r3 = r20
            r4 = r12
            com.android.server.appop.AppOpsService$Op r0 = r1.getOpLocked(r2, r3, r4, r5, r6)     // Catch:{ all -> 0x00f5 }
            r1 = r0
            if (r1 != 0) goto L_0x003f
            monitor-exit(r17)     // Catch:{ all -> 0x0036 }
            return
        L_0x0036:
            r0 = move-exception
            r6 = r11
            r16 = r12
            r2 = r13
            r4 = r14
            r3 = r15
            goto L_0x00fc
        L_0x003f:
            java.util.ArrayList<com.android.server.appop.AppOpsService$Op> r0 = r11.mStartedOps     // Catch:{ all -> 0x00f5 }
            boolean r0 = r0.remove(r1)     // Catch:{ all -> 0x00f5 }
            r2 = 0
            if (r0 != 0) goto L_0x00ca
            long r3 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0036 }
            java.lang.Class<android.content.pm.PackageManagerInternal> r0 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r0 = com.android.server.LocalServices.getService(r0)     // Catch:{ all -> 0x00c5 }
            android.content.pm.PackageManagerInternal r0 = (android.content.pm.PackageManagerInternal) r0     // Catch:{ all -> 0x00c5 }
            int r6 = android.os.UserHandle.getUserId(r20)     // Catch:{ all -> 0x00c5 }
            int r0 = r0.getPackageUid(r12, r2, r6)     // Catch:{ all -> 0x00c5 }
            if (r0 >= 0) goto L_0x008d
            java.lang.String r0 = "AppOps"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c5 }
            r2.<init>()     // Catch:{ all -> 0x00c5 }
            java.lang.String r6 = "Finishing op="
            r2.append(r6)     // Catch:{ all -> 0x00c5 }
            java.lang.String r6 = android.app.AppOpsManager.opToName(r19)     // Catch:{ all -> 0x00c5 }
            r2.append(r6)     // Catch:{ all -> 0x00c5 }
            java.lang.String r6 = " for non-existing package="
            r2.append(r6)     // Catch:{ all -> 0x00c5 }
            r2.append(r12)     // Catch:{ all -> 0x00c5 }
            java.lang.String r6 = " in uid="
            r2.append(r6)     // Catch:{ all -> 0x00c5 }
            r2.append(r14)     // Catch:{ all -> 0x00c5 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00c5 }
            android.util.Slog.i(r0, r2)     // Catch:{ all -> 0x00c5 }
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0036 }
            monitor-exit(r17)     // Catch:{ all -> 0x0036 }
            return
        L_0x008d:
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0036 }
            java.lang.String r0 = "AppOps"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0036 }
            r2.<init>()     // Catch:{ all -> 0x0036 }
            java.lang.String r6 = "Operation not started: uid="
            r2.append(r6)     // Catch:{ all -> 0x0036 }
            com.android.server.appop.AppOpsService$UidState r6 = r1.uidState     // Catch:{ all -> 0x0036 }
            int r6 = r6.uid     // Catch:{ all -> 0x0036 }
            r2.append(r6)     // Catch:{ all -> 0x0036 }
            java.lang.String r6 = " pkg="
            r2.append(r6)     // Catch:{ all -> 0x0036 }
            java.lang.String r6 = r1.packageName     // Catch:{ all -> 0x0036 }
            r2.append(r6)     // Catch:{ all -> 0x0036 }
            java.lang.String r6 = " op="
            r2.append(r6)     // Catch:{ all -> 0x0036 }
            int r6 = r1.op     // Catch:{ all -> 0x0036 }
            java.lang.String r6 = android.app.AppOpsManager.opToName(r6)     // Catch:{ all -> 0x0036 }
            r2.append(r6)     // Catch:{ all -> 0x0036 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0036 }
            android.util.Slog.wtf(r0, r2)     // Catch:{ all -> 0x0036 }
            monitor-exit(r17)     // Catch:{ all -> 0x0036 }
            return
        L_0x00c5:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0036 }
            throw r0     // Catch:{ all -> 0x0036 }
        L_0x00ca:
            r7.finishOperationLocked(r1, r2)     // Catch:{ all -> 0x00f5 }
            com.android.server.AppOpsServiceState r9 = r7.mServiceState     // Catch:{ all -> 0x00f5 }
            r0 = 0
            r3 = 3
            r4 = 0
            r10 = r20
            r6 = r11
            r11 = r12
            r16 = r12
            r12 = r19
            r2 = r13
            r13 = r0
            r14 = r3
            r3 = r15
            r15 = r4
            r9.onAppApplyOperation(r10, r11, r12, r13, r14, r15)     // Catch:{ all -> 0x00f1 }
            int r0 = r1.startNesting     // Catch:{ all -> 0x00f1 }
            if (r0 > 0) goto L_0x00ed
            r4 = r20
            r0 = 0
            r7.scheduleOpActiveChangedIfNeededLocked(r3, r4, r2, r0)     // Catch:{ all -> 0x00fe }
            goto L_0x00ef
        L_0x00ed:
            r4 = r20
        L_0x00ef:
            monitor-exit(r17)     // Catch:{ all -> 0x00fe }
            return
        L_0x00f1:
            r0 = move-exception
            r4 = r20
            goto L_0x00fc
        L_0x00f5:
            r0 = move-exception
            r6 = r11
            r16 = r12
            r2 = r13
            r4 = r14
            r3 = r15
        L_0x00fc:
            monitor-exit(r17)     // Catch:{ all -> 0x00fe }
            throw r0
        L_0x00fe:
            r0 = move-exception
            goto L_0x00fc
        L_0x0100:
            r0 = move-exception
            r6 = r11
            r16 = r12
            r2 = r13
            r4 = r14
            r3 = r15
            r1 = r0
            r0 = r1
            java.lang.String r1 = "AppOps"
            java.lang.String r5 = "Cannot finishOperation"
            android.util.Slog.e(r1, r5, r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.finishOperation(android.os.IBinder, int, int, java.lang.String):void");
    }

    private void scheduleOpActiveChangedIfNeededLocked(int code, int uid, String packageName, boolean active) {
        ArraySet<ActiveCallback> dispatchedCallbacks = null;
        int callbackListCount = this.mActiveWatchers.size();
        for (int i = 0; i < callbackListCount; i++) {
            ActiveCallback callback = this.mActiveWatchers.valueAt(i).get(code);
            if (callback != null && (callback.mWatchingUid < 0 || callback.mWatchingUid == uid)) {
                if (dispatchedCallbacks == null) {
                    dispatchedCallbacks = new ArraySet<>();
                }
                dispatchedCallbacks.add(callback);
            }
        }
        if (dispatchedCallbacks != null) {
            this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppOpsService$ac4Ra3Yhj0OQzvkaL2dLbsuLAmQ.INSTANCE, this, dispatchedCallbacks, Integer.valueOf(code), Integer.valueOf(uid), packageName, Boolean.valueOf(active)));
        }
    }

    /* access modifiers changed from: private */
    public void notifyOpActiveChanged(ArraySet<ActiveCallback> callbacks, int code, int uid, String packageName, boolean active) {
        long identity = Binder.clearCallingIdentity();
        try {
            int callbackCount = callbacks.size();
            for (int i = 0; i < callbackCount; i++) {
                try {
                    callbacks.valueAt(i).mCallback.opActiveChanged(code, uid, packageName, active);
                } catch (RemoteException e) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private void scheduleOpNotedIfNeededLocked(int code, int uid, String packageName, int result) {
        ArraySet<NotedCallback> dispatchedCallbacks = null;
        int callbackListCount = this.mNotedWatchers.size();
        for (int i = 0; i < callbackListCount; i++) {
            NotedCallback callback = this.mNotedWatchers.valueAt(i).get(code);
            if (callback != null && (callback.mWatchingUid < 0 || callback.mWatchingUid == uid)) {
                if (dispatchedCallbacks == null) {
                    dispatchedCallbacks = new ArraySet<>();
                }
                dispatchedCallbacks.add(callback);
            }
        }
        if (dispatchedCallbacks != null) {
            this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppOpsService$AfBLuTvVESlqN91IyRX84hMV5nE.INSTANCE, this, dispatchedCallbacks, Integer.valueOf(code), Integer.valueOf(uid), packageName, Integer.valueOf(result)));
        }
    }

    /* access modifiers changed from: private */
    public void notifyOpChecked(ArraySet<NotedCallback> callbacks, int code, int uid, String packageName, int result) {
        long identity = Binder.clearCallingIdentity();
        try {
            int callbackCount = callbacks.size();
            for (int i = 0; i < callbackCount; i++) {
                try {
                    callbacks.valueAt(i).mCallback.opNoted(code, uid, packageName, result);
                } catch (RemoteException e) {
                }
            }
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    public int permissionToOpCode(String permission) {
        if (permission == null) {
            return -1;
        }
        return AppOpsManager.permissionToOpCode(permission);
    }

    /* access modifiers changed from: package-private */
    public void finishOperationLocked(Op op, boolean finishNested) {
        int i;
        Op op2 = op;
        int opCode = op2.op;
        int uid = op2.uidState.uid;
        if (op2.startNesting <= 1 || finishNested) {
            if (op2.startNesting == 1 || finishNested) {
                long duration = SystemClock.elapsedRealtime() - op2.startRealtime;
                op.finished(System.currentTimeMillis(), duration, op2.uidState.state, 1);
                i = 1;
                int i2 = opCode;
                int i3 = uid;
                this.mHistoricalRegistry.increaseOpAccessDuration(opCode, uid, op2.packageName, op2.uidState.state, 1, duration);
            } else {
                AppOpsManager.OpEntry opEntry = new AppOpsManager.OpEntry(op2.op, op2.running, op.mode, op.mAccessTimes, op.mRejectTimes, op.mDurations, op.mProxyUids, op.mProxyPackageNames);
                Slog.w(TAG, "Finishing op nesting under-run: uid " + uid + " pkg " + op2.packageName + " code " + opCode + " time=" + opEntry.getLastAccessTime(31) + " duration=" + opEntry.getLastDuration(100, 700, 31) + " nesting=" + op2.startNesting);
                i = 1;
                int i4 = opCode;
                int i5 = uid;
            }
            if (op2.startNesting >= i) {
                op2.uidState.startNesting -= op2.startNesting;
            }
            op2.startNesting = 0;
            return;
        }
        op2.startNesting--;
        op2.uidState.startNesting--;
        int i6 = opCode;
        int i7 = uid;
    }

    private void verifyIncomingUid(int uid) {
        if (uid != Binder.getCallingUid() && Binder.getCallingPid() != Process.myPid()) {
            this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        }
    }

    private void verifyIncomingOp(int op) {
        if (op >= 0 && op < 91) {
            return;
        }
        if (op <= 10000 || op >= 10033) {
            throw new IllegalArgumentException("Bad operation #" + op);
        }
    }

    private UidState getUidStateLocked(int uid, boolean edit) {
        UidState uidState = this.mUidStates.get(uid);
        if (uidState == null) {
            if (!edit) {
                return null;
            }
            UidState uidState2 = new UidState(uid);
            this.mUidStates.put(uid, uidState2);
            return uidState2;
        } else if (uidState.pendingStateCommitTime == 0) {
            return uidState;
        } else {
            if (uidState.pendingStateCommitTime < this.mLastRealtime) {
                commitUidPendingStateLocked(uidState);
                return uidState;
            }
            this.mLastRealtime = SystemClock.elapsedRealtime();
            if (uidState.pendingStateCommitTime >= this.mLastRealtime) {
                return uidState;
            }
            commitUidPendingStateLocked(uidState);
            return uidState;
        }
    }

    private void commitUidPendingStateLocked(UidState uidState) {
        ModeCallback callback;
        ArraySet<ModeCallback> callbacks;
        int i;
        int cbi;
        int pkgi;
        UidState uidState2 = uidState;
        if (uidState2.hasForegroundWatchers) {
            boolean z = true;
            int fgi = uidState2.foregroundOps.size() - 1;
            while (fgi >= 0) {
                if (uidState2.foregroundOps.valueAt(fgi)) {
                    int code = uidState2.foregroundOps.keyAt(fgi);
                    long firstUnrestrictedUidState = (long) AppOpsManager.resolveFirstUnrestrictedUidState(code);
                    if ((((long) uidState2.state) <= firstUnrestrictedUidState ? z : false) != (((long) uidState2.pendingState) <= firstUnrestrictedUidState ? z : false)) {
                        ArraySet<ModeCallback> callbacks2 = this.mOpModeWatchers.get(code);
                        if (callbacks2 != null) {
                            int cbi2 = callbacks2.size() - z;
                            while (cbi2 >= 0) {
                                ModeCallback callback2 = callbacks2.valueAt(cbi2);
                                if (callback2.mFlags == false || !z) {
                                } else if (callback2.isWatchingUid(uidState2.uid)) {
                                    int i2 = 4;
                                    boolean doAllPackages = (uidState2.opModes == null || uidState2.opModes.indexOfKey(code) < 0 || uidState2.opModes.get(code) != 4) ? false : z;
                                    if (uidState2.pkgOps != null) {
                                        int pkgi2 = uidState2.pkgOps.size() - z;
                                        while (pkgi2 >= 0) {
                                            Op op = (Op) uidState2.pkgOps.valueAt(pkgi2).get(code);
                                            if (op == null) {
                                                pkgi = pkgi2;
                                                i = i2;
                                                callback = callback2;
                                                cbi = cbi2;
                                                callbacks = callbacks2;
                                            } else if (doAllPackages || op.mode == i2) {
                                                pkgi = pkgi2;
                                                i = 4;
                                                callback = callback2;
                                                cbi = cbi2;
                                                callbacks = callbacks2;
                                                this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppOpsService$FYLTtxqrHmv8Y5UdZ9ybXKsSJhs.INSTANCE, this, callback2, Integer.valueOf(code), Integer.valueOf(uidState2.uid), uidState2.pkgOps.keyAt(pkgi2)));
                                            } else {
                                                pkgi = pkgi2;
                                                i = i2;
                                                callback = callback2;
                                                cbi = cbi2;
                                                callbacks = callbacks2;
                                            }
                                            pkgi2 = pkgi - 1;
                                            cbi2 = cbi;
                                            i2 = i;
                                            callbacks2 = callbacks;
                                            callback2 = callback;
                                        }
                                        int i3 = pkgi2;
                                        ModeCallback modeCallback = callback2;
                                    }
                                }
                                cbi2--;
                                callbacks2 = callbacks2;
                                z = true;
                            }
                            int i4 = cbi2;
                            ArraySet<ModeCallback> arraySet = callbacks2;
                        }
                    }
                }
                fgi--;
                z = true;
            }
        }
        uidState2.state = uidState2.pendingState;
        uidState2.pendingStateCommitTime = 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0022, code lost:
        r1 = false;
        r2 = android.os.Binder.clearCallingIdentity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        r4 = ((android.content.pm.PackageManagerInternal) com.android.server.LocalServices.getService(android.content.pm.PackageManagerInternal.class)).getApplicationInfo(r10, 546054144, 1000, android.os.UserHandle.getUserId(r9));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003c, code lost:
        if (r4 == null) goto L_0x0049;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x003e, code lost:
        r5 = r4.uid;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0044, code lost:
        if ((r4.privateFlags & 8) == 0) goto L_0x0047;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0046, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0047, code lost:
        r1 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004d, code lost:
        r5 = resolveUid(r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004e, code lost:
        if (r5 < 0) goto L_0x0051;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0050, code lost:
        r1 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0051, code lost:
        if (r5 != r9) goto L_0x0058;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0057, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007e, code lost:
        throw new java.lang.SecurityException("Specified package " + r10 + " under uid " + r9 + " but it is really " + r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x007f, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0080, code lost:
        android.os.Binder.restoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0083, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean verifyAndGetIsPrivileged(int r9, java.lang.String r10) {
        /*
            r8 = this;
            r0 = 0
            if (r9 != 0) goto L_0x0004
            return r0
        L_0x0004:
            monitor-enter(r8)
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r1 = r8.mUidStates     // Catch:{ all -> 0x0084 }
            java.lang.Object r1 = r1.get(r9)     // Catch:{ all -> 0x0084 }
            com.android.server.appop.AppOpsService$UidState r1 = (com.android.server.appop.AppOpsService.UidState) r1     // Catch:{ all -> 0x0084 }
            if (r1 == 0) goto L_0x0021
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r2 = r1.pkgOps     // Catch:{ all -> 0x0084 }
            if (r2 == 0) goto L_0x0021
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r2 = r1.pkgOps     // Catch:{ all -> 0x0084 }
            java.lang.Object r2 = r2.get(r10)     // Catch:{ all -> 0x0084 }
            com.android.server.appop.AppOpsService$Ops r2 = (com.android.server.appop.AppOpsService.Ops) r2     // Catch:{ all -> 0x0084 }
            if (r2 == 0) goto L_0x0021
            boolean r0 = r2.isPrivileged     // Catch:{ all -> 0x0084 }
            monitor-exit(r8)     // Catch:{ all -> 0x0084 }
            return r0
        L_0x0021:
            monitor-exit(r8)     // Catch:{ all -> 0x0084 }
            r1 = 0
            long r2 = android.os.Binder.clearCallingIdentity()
            java.lang.Class<android.content.pm.PackageManagerInternal> r4 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r4 = com.android.server.LocalServices.getService(r4)     // Catch:{ all -> 0x007f }
            android.content.pm.PackageManagerInternal r4 = (android.content.pm.PackageManagerInternal) r4     // Catch:{ all -> 0x007f }
            r5 = 546054144(0x208c2000, float:2.3738098E-19)
            r6 = 1000(0x3e8, float:1.401E-42)
            int r7 = android.os.UserHandle.getUserId(r9)     // Catch:{ all -> 0x007f }
            android.content.pm.ApplicationInfo r4 = r4.getApplicationInfo(r10, r5, r6, r7)     // Catch:{ all -> 0x007f }
            if (r4 == 0) goto L_0x0049
            int r5 = r4.uid     // Catch:{ all -> 0x007f }
            int r6 = r4.privateFlags     // Catch:{ all -> 0x007f }
            r6 = r6 & 8
            if (r6 == 0) goto L_0x0047
            r0 = 1
        L_0x0047:
            r1 = r0
            goto L_0x0051
        L_0x0049:
            int r0 = resolveUid(r10)     // Catch:{ all -> 0x007f }
            r5 = r0
            if (r5 < 0) goto L_0x0051
            r1 = 0
        L_0x0051:
            if (r5 != r9) goto L_0x0058
            android.os.Binder.restoreCallingIdentity(r2)
            return r1
        L_0x0058:
            java.lang.SecurityException r0 = new java.lang.SecurityException     // Catch:{ all -> 0x007f }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x007f }
            r6.<init>()     // Catch:{ all -> 0x007f }
            java.lang.String r7 = "Specified package "
            r6.append(r7)     // Catch:{ all -> 0x007f }
            r6.append(r10)     // Catch:{ all -> 0x007f }
            java.lang.String r7 = " under uid "
            r6.append(r7)     // Catch:{ all -> 0x007f }
            r6.append(r9)     // Catch:{ all -> 0x007f }
            java.lang.String r7 = " but it is really "
            r6.append(r7)     // Catch:{ all -> 0x007f }
            r6.append(r5)     // Catch:{ all -> 0x007f }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x007f }
            r0.<init>(r6)     // Catch:{ all -> 0x007f }
            throw r0     // Catch:{ all -> 0x007f }
        L_0x007f:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r2)
            throw r0
        L_0x0084:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0084 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.verifyAndGetIsPrivileged(int, java.lang.String):boolean");
    }

    private Ops getOpsRawLocked(int uid, String packageName, boolean isPrivileged, boolean edit) {
        UidState uidState = getUidStateLocked(uid, edit);
        if (uidState == null) {
            return null;
        }
        if (uidState.pkgOps == null) {
            if (!edit) {
                return null;
            }
            uidState.pkgOps = new ArrayMap<>();
        }
        Ops ops = uidState.pkgOps.get(packageName);
        if (ops != null) {
            return ops;
        }
        if (!edit) {
            return null;
        }
        Ops ops2 = new Ops(packageName, uidState, isPrivileged);
        uidState.pkgOps.put(packageName, ops2);
        return ops2;
    }

    private Ops getOpsRawNoVerifyLocked(int uid, String packageName, boolean edit, boolean isPrivileged) {
        UidState uidState = getUidStateLocked(uid, edit);
        if (uidState == null) {
            return null;
        }
        if (uidState.pkgOps == null) {
            if (!edit) {
                return null;
            }
            uidState.pkgOps = new ArrayMap<>();
        }
        Ops ops = uidState.pkgOps.get(packageName);
        if (ops != null) {
            return ops;
        }
        if (!edit) {
            return null;
        }
        Ops ops2 = new Ops(packageName, uidState, isPrivileged);
        uidState.pkgOps.put(packageName, ops2);
        return ops2;
    }

    private void scheduleWriteLocked() {
        if (!this.mWriteScheduled) {
            this.mWriteScheduled = true;
            this.mHandler.postDelayed(this.mWriteRunner, 1800000);
        }
    }

    private void scheduleFastWriteLocked() {
        if (!this.mFastWriteScheduled) {
            this.mWriteScheduled = true;
            this.mFastWriteScheduled = true;
            this.mHandler.removeCallbacks(this.mWriteRunner);
            this.mHandler.postDelayed(this.mWriteRunner, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
        }
    }

    private Op getOpLocked(int code, int uid, String packageName, boolean isPrivileged, boolean edit) {
        Ops ops = getOpsRawNoVerifyLocked(uid, packageName, edit, isPrivileged);
        if (ops == null) {
            return null;
        }
        return getOpLocked(ops, code, edit);
    }

    private Op getOpLocked(Ops ops, int code, boolean edit) {
        Op op = (Op) ops.get(code);
        if (op == null) {
            if (!edit) {
                return null;
            }
            op = new Op(ops.uidState, ops.packageName, code);
            ops.put(code, op);
        }
        if (edit) {
            scheduleWriteLocked();
        }
        return op;
    }

    private boolean isOpRestrictedDueToSuspend(int code, String packageName, int uid) {
        if (!ArrayUtils.contains(OPS_RESTRICTED_ON_SUSPEND, code)) {
            return false;
        }
        return ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).isPackageSuspended(packageName, UserHandle.getUserId(uid));
    }

    private boolean isOpRestrictedLocked(int uid, int code, String packageName, boolean isPrivileged) {
        if (code > 10000) {
            return false;
        }
        int userHandle = UserHandle.getUserId(uid);
        int restrictionSetCount = this.mOpUserRestrictions.size();
        for (int i = 0; i < restrictionSetCount; i++) {
            if (this.mOpUserRestrictions.valueAt(i).hasRestriction(code, packageName, userHandle)) {
                if (AppOpsManager.opAllowSystemBypassRestriction(code)) {
                    synchronized (this) {
                        Ops ops = getOpsRawLocked(uid, packageName, isPrivileged, true);
                        if (ops != null && ops.isPrivileged) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 13 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x002d A[Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }] */
    /* JADX WARNING: Removed duplicated region for block: B:44:0x00a2 A[SYNTHETIC, Splitter:B:44:0x00a2] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:64:0x00f0=Splitter:B:64:0x00f0, B:73:0x0113=Splitter:B:73:0x0113, B:82:0x0136=Splitter:B:82:0x0136, B:41:0x009a=Splitter:B:41:0x009a, B:91:0x015a=Splitter:B:91:0x015a, B:55:0x00cd=Splitter:B:55:0x00cd, B:100:0x017e=Splitter:B:100:0x017e} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void readState() {
        /*
            r13 = this;
            r0 = -1
            android.util.AtomicFile r1 = r13.mFile
            monitor-enter(r1)
            monitor-enter(r13)     // Catch:{ all -> 0x01c5 }
            android.util.AtomicFile r2 = r13.mFile     // Catch:{ FileNotFoundException -> 0x019e }
            java.io.FileInputStream r2 = r2.openRead()     // Catch:{ FileNotFoundException -> 0x019e }
            r3 = 0
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r4 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r4.clear()     // Catch:{ all -> 0x019c }
            org.xmlpull.v1.XmlPullParser r4 = android.util.Xml.newPullParser()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            java.nio.charset.Charset r5 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            java.lang.String r5 = r5.name()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            r4.setInput(r2, r5)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
        L_0x001f:
            int r5 = r4.next()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            r6 = r5
            r7 = 1
            r8 = 2
            if (r5 == r8) goto L_0x002b
            if (r6 == r7) goto L_0x002b
            goto L_0x001f
        L_0x002b:
            if (r6 != r8) goto L_0x00a2
            r5 = 0
            java.lang.String r8 = "v"
            java.lang.String r5 = r4.getAttributeValue(r5, r8)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            if (r5 == 0) goto L_0x003c
            int r8 = java.lang.Integer.parseInt(r5)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            r0 = r8
        L_0x003c:
            int r8 = r4.getDepth()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
        L_0x0040:
            int r9 = r4.next()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            r6 = r9
            if (r9 == r7) goto L_0x0092
            r9 = 3
            if (r6 != r9) goto L_0x0050
            int r10 = r4.getDepth()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            if (r10 <= r8) goto L_0x0092
        L_0x0050:
            if (r6 == r9) goto L_0x0040
            r9 = 4
            if (r6 != r9) goto L_0x0056
            goto L_0x0040
        L_0x0056:
            java.lang.String r9 = r4.getName()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            java.lang.String r10 = "pkg"
            boolean r10 = r9.equals(r10)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            if (r10 == 0) goto L_0x0067
            r13.readPackage(r4)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            goto L_0x0091
        L_0x0067:
            java.lang.String r10 = "uid"
            boolean r10 = r9.equals(r10)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            if (r10 == 0) goto L_0x0074
            r13.readUidOps(r4)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            goto L_0x0091
        L_0x0074:
            java.lang.String r10 = "AppOps"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            r11.<init>()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            java.lang.String r12 = "Unknown element under <app-ops>: "
            r11.append(r12)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            java.lang.String r12 = r4.getName()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            r11.append(r12)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            java.lang.String r11 = r11.toString()     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            android.util.Slog.w(r10, r11)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            com.android.internal.util.XmlUtils.skipCurrentTag(r4)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
        L_0x0091:
            goto L_0x0040
        L_0x0092:
            r3 = 1
            if (r3 != 0) goto L_0x009a
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r4 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r4.clear()     // Catch:{ all -> 0x019c }
        L_0x009a:
            r2.close()     // Catch:{ IOException -> 0x009f }
        L_0x009d:
            goto L_0x0183
        L_0x009f:
            r4 = move-exception
            goto L_0x0183
        L_0x00a2:
            java.lang.IllegalStateException r5 = new java.lang.IllegalStateException     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            java.lang.String r7 = "no start tag found"
            r5.<init>(r7)     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
            throw r5     // Catch:{ IllegalStateException -> 0x015f, NullPointerException -> 0x013b, NumberFormatException -> 0x0117, XmlPullParserException -> 0x00f4, IOException -> 0x00d1, IndexOutOfBoundsException -> 0x00ae }
        L_0x00ab:
            r4 = move-exception
            goto L_0x018e
        L_0x00ae:
            r4 = move-exception
            java.lang.String r5 = "AppOps"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r6.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r7 = "Failed parsing "
            r6.append(r7)     // Catch:{ all -> 0x00ab }
            r6.append(r4)     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ab }
            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x00ab }
            if (r3 != 0) goto L_0x00cd
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r4 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r4.clear()     // Catch:{ all -> 0x019c }
        L_0x00cd:
            r2.close()     // Catch:{ IOException -> 0x009f }
            goto L_0x009d
        L_0x00d1:
            r4 = move-exception
            java.lang.String r5 = "AppOps"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r6.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r7 = "Failed parsing "
            r6.append(r7)     // Catch:{ all -> 0x00ab }
            r6.append(r4)     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ab }
            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x00ab }
            if (r3 != 0) goto L_0x00f0
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r4 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r4.clear()     // Catch:{ all -> 0x019c }
        L_0x00f0:
            r2.close()     // Catch:{ IOException -> 0x009f }
            goto L_0x009d
        L_0x00f4:
            r4 = move-exception
            java.lang.String r5 = "AppOps"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r6.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r7 = "Failed parsing "
            r6.append(r7)     // Catch:{ all -> 0x00ab }
            r6.append(r4)     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ab }
            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x00ab }
            if (r3 != 0) goto L_0x0113
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r4 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r4.clear()     // Catch:{ all -> 0x019c }
        L_0x0113:
            r2.close()     // Catch:{ IOException -> 0x009f }
            goto L_0x009d
        L_0x0117:
            r4 = move-exception
            java.lang.String r5 = "AppOps"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r6.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r7 = "Failed parsing "
            r6.append(r7)     // Catch:{ all -> 0x00ab }
            r6.append(r4)     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ab }
            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x00ab }
            if (r3 != 0) goto L_0x0136
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r4 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r4.clear()     // Catch:{ all -> 0x019c }
        L_0x0136:
            r2.close()     // Catch:{ IOException -> 0x009f }
            goto L_0x009d
        L_0x013b:
            r4 = move-exception
            java.lang.String r5 = "AppOps"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r6.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r7 = "Failed parsing "
            r6.append(r7)     // Catch:{ all -> 0x00ab }
            r6.append(r4)     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ab }
            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x00ab }
            if (r3 != 0) goto L_0x015a
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r4 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r4.clear()     // Catch:{ all -> 0x019c }
        L_0x015a:
            r2.close()     // Catch:{ IOException -> 0x009f }
            goto L_0x009d
        L_0x015f:
            r4 = move-exception
            java.lang.String r5 = "AppOps"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r6.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r7 = "Failed parsing "
            r6.append(r7)     // Catch:{ all -> 0x00ab }
            r6.append(r4)     // Catch:{ all -> 0x00ab }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00ab }
            android.util.Slog.w(r5, r6)     // Catch:{ all -> 0x00ab }
            if (r3 != 0) goto L_0x017e
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r4 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r4.clear()     // Catch:{ all -> 0x019c }
        L_0x017e:
            r2.close()     // Catch:{ IOException -> 0x009f }
            goto L_0x009d
        L_0x0183:
            monitor-exit(r13)     // Catch:{ all -> 0x019c }
            monitor-exit(r1)     // Catch:{ all -> 0x01c5 }
            monitor-enter(r13)
            r13.upgradeLocked(r0)     // Catch:{ all -> 0x018b }
            monitor-exit(r13)     // Catch:{ all -> 0x018b }
            return
        L_0x018b:
            r1 = move-exception
            monitor-exit(r13)     // Catch:{ all -> 0x018b }
            throw r1
        L_0x018e:
            if (r3 != 0) goto L_0x0195
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r5 = r13.mUidStates     // Catch:{ all -> 0x019c }
            r5.clear()     // Catch:{ all -> 0x019c }
        L_0x0195:
            r2.close()     // Catch:{ IOException -> 0x0199 }
            goto L_0x019a
        L_0x0199:
            r5 = move-exception
        L_0x019a:
            throw r4     // Catch:{ all -> 0x019c }
        L_0x019c:
            r2 = move-exception
            goto L_0x01c3
        L_0x019e:
            r2 = move-exception
            java.lang.String r3 = "AppOps"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x019c }
            r4.<init>()     // Catch:{ all -> 0x019c }
            java.lang.String r5 = "No existing app ops "
            r4.append(r5)     // Catch:{ all -> 0x019c }
            android.util.AtomicFile r5 = r13.mFile     // Catch:{ all -> 0x019c }
            java.io.File r5 = r5.getBaseFile()     // Catch:{ all -> 0x019c }
            r4.append(r5)     // Catch:{ all -> 0x019c }
            java.lang.String r5 = "; starting empty"
            r4.append(r5)     // Catch:{ all -> 0x019c }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x019c }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x019c }
            monitor-exit(r13)     // Catch:{ all -> 0x019c }
            monitor-exit(r1)     // Catch:{ all -> 0x01c5 }
            return
        L_0x01c3:
            monitor-exit(r13)     // Catch:{ all -> 0x019c }
            throw r2     // Catch:{ all -> 0x01c5 }
        L_0x01c5:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x01c5 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.readState():void");
    }

    private void upgradeRunAnyInBackgroundLocked() {
        Op op;
        int idx;
        for (int i = 0; i < this.mUidStates.size(); i++) {
            UidState uidState = this.mUidStates.valueAt(i);
            if (uidState != null) {
                if (uidState.opModes != null && (idx = uidState.opModes.indexOfKey(63)) >= 0) {
                    uidState.opModes.put(70, uidState.opModes.valueAt(idx));
                }
                if (uidState.pkgOps != null) {
                    boolean changed = false;
                    for (int j = 0; j < uidState.pkgOps.size(); j++) {
                        Ops ops = uidState.pkgOps.valueAt(j);
                        if (!(ops == null || (op = (Op) ops.get(63)) == null || op.mode == AppOpsManager.opToDefaultMode(op.op))) {
                            Op copy = new Op(op.uidState, op.packageName, 70);
                            int unused = copy.mode = op.mode;
                            ops.put(70, copy);
                            changed = true;
                        }
                    }
                    if (changed) {
                        uidState.evalForegroundOps(this.mOpModeWatchers);
                    }
                }
            }
        }
    }

    private void upgradeLocked(int oldVersion) {
        if (oldVersion < 1) {
            Slog.d(TAG, "Upgrading app-ops xml from version " + oldVersion + " to " + 1);
            if (oldVersion == -1) {
                upgradeRunAnyInBackgroundLocked();
            }
            scheduleFastWriteLocked();
        }
    }

    private void readUidOps(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException {
        int uid = Integer.parseInt(parser.getAttributeValue((String) null, "n"));
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals("op")) {
                    int code = Integer.parseInt(parser.getAttributeValue((String) null, "n"));
                    int mode = Integer.parseInt(parser.getAttributeValue((String) null, "m"));
                    UidState uidState = getUidStateLocked(uid, true);
                    if (uidState.opModes == null) {
                        uidState.opModes = new SparseIntArray();
                    }
                    uidState.opModes.put(code, mode);
                } else {
                    Slog.w(TAG, "Unknown element under <uid-ops>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    private void readPackage(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException {
        String pkgName = parser.getAttributeValue((String) null, "n");
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1) {
                return;
            }
            if (type == 3 && parser.getDepth() <= outerDepth) {
                return;
            }
            if (!(type == 3 || type == 4)) {
                if (parser.getName().equals(WatchlistLoggingHandler.WatchlistEventKeys.UID)) {
                    readUid(parser, pkgName);
                } else {
                    Slog.w(TAG, "Unknown element under <pkg>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
    }

    private void readUid(XmlPullParser parser, String pkgName) throws NumberFormatException, XmlPullParserException, IOException {
        int uid = Integer.parseInt(parser.getAttributeValue((String) null, "n"));
        UidState uidState = getUidStateLocked(uid, true);
        String isPrivilegedString = parser.getAttributeValue((String) null, "p");
        boolean isPrivileged = false;
        if (isPrivilegedString == null) {
            try {
                if (ActivityThread.getPackageManager() != null) {
                    boolean z = false;
                    ApplicationInfo appInfo = ActivityThread.getPackageManager().getApplicationInfo(pkgName, 0, UserHandle.getUserId(uid));
                    if (appInfo != null) {
                        if ((appInfo.privateFlags & 8) != 0) {
                            z = true;
                        }
                        isPrivileged = z;
                    }
                } else {
                    return;
                }
            } catch (RemoteException e) {
                Slog.w(TAG, "Could not contact PackageManager", e);
            }
        } else {
            isPrivileged = Boolean.parseBoolean(isPrivilegedString);
        }
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next == 1 || (type == 3 && parser.getDepth() <= outerDepth)) {
                uidState.evalForegroundOps(this.mOpModeWatchers);
            } else if (!(type == 3 || type == 4)) {
                if (parser.getName().equals("op")) {
                    readOp(parser, uidState, pkgName, isPrivileged);
                } else {
                    Slog.w(TAG, "Unknown element under <pkg>: " + parser.getName());
                    XmlUtils.skipCurrentTag(parser);
                }
            }
        }
        uidState.evalForegroundOps(this.mOpModeWatchers);
    }

    private void readOp(XmlPullParser parser, UidState uidState, String pkgName, boolean isPrivileged) throws NumberFormatException, XmlPullParserException, IOException {
        long j;
        XmlPullParser xmlPullParser = parser;
        UidState uidState2 = uidState;
        String str = pkgName;
        Op op = new Op(uidState2, str, Integer.parseInt(xmlPullParser.getAttributeValue((String) null, "n")));
        int unused = op.mode = XmlUtils.readIntAttribute(xmlPullParser, "m", AppOpsManager.opToDefaultMode(op.op));
        int outerDepth = parser.getDepth();
        while (true) {
            int next = parser.next();
            int type = next;
            if (next != 1 && (type != 3 || parser.getDepth() > outerDepth)) {
                if (!(type == 3 || type == 4)) {
                    if (parser.getName().equals("st")) {
                        long key = XmlUtils.readLongAttribute(xmlPullParser, "n");
                        int flags = AppOpsManager.extractFlagsFromKey(key);
                        int state = AppOpsManager.extractUidStateFromKey(key);
                        long accessTime = XmlUtils.readLongAttribute(xmlPullParser, "t", 0);
                        long rejectTime = XmlUtils.readLongAttribute(xmlPullParser, ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD, 0);
                        long accessDuration = XmlUtils.readLongAttribute(xmlPullParser, "d", 0);
                        String proxyPkg = XmlUtils.readStringAttribute(xmlPullParser, "pp");
                        int proxyUid = XmlUtils.readIntAttribute(xmlPullParser, "pu", 0);
                        if (accessTime > 0) {
                            j = 0;
                            op.accessed(accessTime, proxyUid, proxyPkg, state, flags);
                        } else {
                            j = 0;
                        }
                        if (rejectTime > j) {
                            op.rejected(rejectTime, proxyUid, proxyPkg, state, flags);
                        }
                        if (accessDuration > j) {
                            op.running(accessTime, accessDuration, state, flags);
                        }
                    } else {
                        Slog.w(TAG, "Unknown element under <op>: " + parser.getName());
                        XmlUtils.skipCurrentTag(parser);
                    }
                }
            }
        }
        if (uidState2.pkgOps == null) {
            uidState2.pkgOps = new ArrayMap<>();
        }
        Ops ops = uidState2.pkgOps.get(str);
        if (ops == null) {
            ops = new Ops(str, uidState2, isPrivileged);
            uidState2.pkgOps.put(str, ops);
        } else {
            boolean z = isPrivileged;
        }
        ops.put(op.op, op);
    }

    /* Debug info: failed to restart local var, previous not found, register: 33 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x0217, code lost:
        if (r26 < 0) goto L_0x0224;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x0219, code lost:
        r5.attribute((java.lang.String) null, "pu", java.lang.Integer.toString(r26));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x0224, code lost:
        r5.endTag((java.lang.String) null, "st");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:103:0x022b, code lost:
        r15 = r15 + 1;
        r4 = r27;
        r6 = r31;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x0235, code lost:
        r27 = r4;
        r31 = r6;
        r5.endTag((java.lang.String) null, "op");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x0241, code lost:
        r27 = r4;
        r31 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0245, code lost:
        r5.endTag((java.lang.String) null, "op");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x024d, code lost:
        r11 = r11 + 1;
        r4 = r27;
        r6 = r31;
        r0 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x0257, code lost:
        r27 = r4;
        r31 = r6;
        r5.endTag((java.lang.String) null, com.android.server.net.watchlist.WatchlistLoggingHandler.WatchlistEventKeys.UID);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x0262, code lost:
        r8 = r8 + 1;
        r4 = r27;
        r6 = r31;
        r0 = null;
        r7 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x026d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x026e, code lost:
        r27 = r4;
        r31 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:?, code lost:
        monitor-exit(r33);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:?, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x0274, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x0276, code lost:
        r27 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x0278, code lost:
        if (r6 == null) goto L_0x0284;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x027a, code lost:
        r5.endTag((java.lang.String) null, android.server.am.SplitScreenReporter.STR_PKG);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x0282, code lost:
        r27 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x0284, code lost:
        r5.endTag((java.lang.String) null, "app-ops");
        r5.endDocument();
        r1.mFile.finishWrite(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x0298, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x029c, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:132:0x029d, code lost:
        r27 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:?, code lost:
        android.util.Slog.w(TAG, "Failed to write state, restoring backup.", r0);
        r1.mFile.failWrite(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00ab, code lost:
        if (r4 == null) goto L_0x0282;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00ad, code lost:
        r6 = null;
        r7 = false;
        r8 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00b4, code lost:
        if (r8 >= r4.size()) goto L_0x0276;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00b6, code lost:
        r9 = r4.get(r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00c4, code lost:
        if (r9.getPackageName().equals(r6) != false) goto L_0x00e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00c6, code lost:
        if (r6 == null) goto L_0x00ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        r5.endTag(r0, android.server.am.SplitScreenReporter.STR_PKG);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00ce, code lost:
        r6 = r9.getPackageName();
        r5.startTag(r0, android.server.am.SplitScreenReporter.STR_PKG);
        r5.attribute(r0, "n", r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00e0, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00e1, code lost:
        r27 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        r5.startTag(r0, com.android.server.net.watchlist.WatchlistLoggingHandler.WatchlistEventKeys.UID);
        r5.attribute(r0, "n", java.lang.Integer.toString(r9.getUid()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00f9, code lost:
        monitor-enter(r33);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:?, code lost:
        r10 = getOpsRawLocked(r9.getUid(), r9.getPackageName(), r7, r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x0106, code lost:
        if (r10 == null) goto L_0x011c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:?, code lost:
        r5.attribute(r0, "p", java.lang.Boolean.toString(r10.isPrivileged));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x0115, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0116, code lost:
        r27 = r4;
        r31 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        r5.attribute(r0, "p", java.lang.Boolean.toString(r7));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0126, code lost:
        monitor-exit(r33);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        r10 = r9.getOps();
        r11 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0130, code lost:
        if (r11 >= r10.size()) goto L_0x0257;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x0132, code lost:
        r12 = r10.get(r11);
        r5.startTag(r0, "op");
        r5.attribute(r0, "n", java.lang.Integer.toString(r12.getOp()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x0158, code lost:
        if (r12.getMode() == android.app.AppOpsManager.opToDefaultMode(r12.getOp())) goto L_0x0168;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:?, code lost:
        r5.attribute(r0, "m", java.lang.Integer.toString(r12.getMode()));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:?, code lost:
        r13 = r12.collectKeys();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x016c, code lost:
        if (r13 == null) goto L_0x0241;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0172, code lost:
        if (r13.size() > 0) goto L_0x017a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x0174, code lost:
        r27 = r4;
        r31 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x017a, code lost:
        r14 = r13.size();
        r15 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x017f, code lost:
        if (r15 >= r14) goto L_0x0235;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0181, code lost:
        r16 = r13.keyAt(r15);
        r7 = android.app.AppOpsManager.extractUidStateFromKey(r16);
        r0 = android.app.AppOpsManager.extractFlagsFromKey(r16);
        r20 = r12.getLastAccessTime(r7, r7, r0);
        r22 = r12.getLastRejectTime(r7, r7, r0);
        r24 = r12.getLastDuration(r7, r7, r0);
        r27 = r12.getProxyPackageName(r7, r0);
        r26 = r12.getProxyUid(r7, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:77:0x01af, code lost:
        if (r20 > 0) goto L_0x01c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x01b3, code lost:
        if (r22 > 0) goto L_0x01c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x01b7, code lost:
        if (r24 > 0) goto L_0x01c6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01b9, code lost:
        r30 = r0;
        r0 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:83:0x01bd, code lost:
        if (r0 != null) goto L_0x01ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x01bf, code lost:
        if (r26 >= 0) goto L_0x01ca;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x01c1, code lost:
        r27 = r4;
        r31 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x01c6, code lost:
        r30 = r0;
        r0 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x01ca, code lost:
        r27 = r4;
        r31 = r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:?, code lost:
        r5.startTag((java.lang.String) null, "st");
        r32 = r7;
        r5.attribute((java.lang.String) null, "n", java.lang.Long.toString(r16));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x01e4, code lost:
        if (r20 <= 0) goto L_0x01f1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x01e6, code lost:
        r5.attribute((java.lang.String) null, "t", java.lang.Long.toString(r20));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x01f3, code lost:
        if (r22 <= 0) goto L_0x0200;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x01f5, code lost:
        r5.attribute((java.lang.String) null, com.android.server.wm.ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD, java.lang.Long.toString(r22));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x0202, code lost:
        if (r24 <= 0) goto L_0x020e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x0204, code lost:
        r5.attribute((java.lang.String) null, "d", java.lang.Long.toString(r24));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x020e, code lost:
        if (r0 == null) goto L_0x0217;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:99:0x0210, code lost:
        r5.attribute((java.lang.String) null, "pp", r0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeState() {
        /*
            r33 = this;
            r1 = r33
            android.util.AtomicFile r2 = r1.mFile
            monitor-enter(r2)
            android.util.AtomicFile r0 = r1.mFile     // Catch:{ IOException -> 0x02af }
            java.io.FileOutputStream r0 = r0.startWrite()     // Catch:{ IOException -> 0x02af }
            r3 = r0
            r0 = 0
            java.util.List r4 = r1.getPackagesForOps(r0)     // Catch:{ all -> 0x02ad }
            com.android.internal.util.FastXmlSerializer r5 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x029c }
            r5.<init>()     // Catch:{ IOException -> 0x029c }
            java.nio.charset.Charset r6 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x029c }
            java.lang.String r6 = r6.name()     // Catch:{ IOException -> 0x029c }
            r5.setOutput(r3, r6)     // Catch:{ IOException -> 0x029c }
            r6 = 1
            java.lang.Boolean r7 = java.lang.Boolean.valueOf(r6)     // Catch:{ IOException -> 0x029c }
            r5.startDocument(r0, r7)     // Catch:{ IOException -> 0x029c }
            java.lang.String r7 = "app-ops"
            r5.startTag(r0, r7)     // Catch:{ IOException -> 0x029c }
            java.lang.String r7 = "v"
            java.lang.String r6 = java.lang.String.valueOf(r6)     // Catch:{ IOException -> 0x029c }
            r5.attribute(r0, r7, r6)     // Catch:{ IOException -> 0x029c }
            monitor-enter(r33)     // Catch:{ IOException -> 0x029c }
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r6 = r1.mUidStates     // Catch:{ all -> 0x0293 }
            int r6 = r6.size()     // Catch:{ all -> 0x0293 }
            r7 = 0
        L_0x003f:
            if (r7 >= r6) goto L_0x00aa
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r8 = r1.mUidStates     // Catch:{ all -> 0x00a5 }
            java.lang.Object r8 = r8.valueAt(r7)     // Catch:{ all -> 0x00a5 }
            com.android.server.appop.AppOpsService$UidState r8 = (com.android.server.appop.AppOpsService.UidState) r8     // Catch:{ all -> 0x00a5 }
            android.util.SparseIntArray r9 = r8.opModes     // Catch:{ all -> 0x00a5 }
            if (r9 == 0) goto L_0x00a2
            android.util.SparseIntArray r9 = r8.opModes     // Catch:{ all -> 0x00a5 }
            int r9 = r9.size()     // Catch:{ all -> 0x00a5 }
            if (r9 <= 0) goto L_0x00a2
            java.lang.String r9 = "uid"
            r5.startTag(r0, r9)     // Catch:{ all -> 0x00a5 }
            java.lang.String r9 = "n"
            int r10 = r8.uid     // Catch:{ all -> 0x00a5 }
            java.lang.String r10 = java.lang.Integer.toString(r10)     // Catch:{ all -> 0x00a5 }
            r5.attribute(r0, r9, r10)     // Catch:{ all -> 0x00a5 }
            android.util.SparseIntArray r9 = r8.opModes     // Catch:{ all -> 0x00a5 }
            int r10 = r9.size()     // Catch:{ all -> 0x00a5 }
            r11 = 0
        L_0x006e:
            if (r11 >= r10) goto L_0x009c
            int r12 = r9.keyAt(r11)     // Catch:{ all -> 0x00a5 }
            int r13 = r9.valueAt(r11)     // Catch:{ all -> 0x00a5 }
            java.lang.String r14 = "op"
            r5.startTag(r0, r14)     // Catch:{ all -> 0x00a5 }
            java.lang.String r14 = "n"
            java.lang.String r15 = java.lang.Integer.toString(r12)     // Catch:{ all -> 0x00a5 }
            r5.attribute(r0, r14, r15)     // Catch:{ all -> 0x00a5 }
            java.lang.String r14 = "m"
            java.lang.String r15 = java.lang.Integer.toString(r13)     // Catch:{ all -> 0x00a5 }
            r5.attribute(r0, r14, r15)     // Catch:{ all -> 0x00a5 }
            java.lang.String r14 = "op"
            r5.endTag(r0, r14)     // Catch:{ all -> 0x00a5 }
            int r11 = r11 + 1
            goto L_0x006e
        L_0x009c:
            java.lang.String r11 = "uid"
            r5.endTag(r0, r11)     // Catch:{ all -> 0x00a5 }
        L_0x00a2:
            int r7 = r7 + 1
            goto L_0x003f
        L_0x00a5:
            r0 = move-exception
            r27 = r4
            goto L_0x0296
        L_0x00aa:
            monitor-exit(r33)     // Catch:{ all -> 0x0293 }
            if (r4 == 0) goto L_0x0282
            r6 = 0
            r7 = 0
            r8 = r7
        L_0x00b0:
            int r9 = r4.size()     // Catch:{ IOException -> 0x029c }
            if (r8 >= r9) goto L_0x0276
            java.lang.Object r9 = r4.get(r8)     // Catch:{ IOException -> 0x029c }
            android.app.AppOpsManager$PackageOps r9 = (android.app.AppOpsManager.PackageOps) r9     // Catch:{ IOException -> 0x029c }
            java.lang.String r10 = r9.getPackageName()     // Catch:{ IOException -> 0x029c }
            boolean r10 = r10.equals(r6)     // Catch:{ IOException -> 0x029c }
            if (r10 != 0) goto L_0x00e5
            if (r6 == 0) goto L_0x00ce
            java.lang.String r10 = "pkg"
            r5.endTag(r0, r10)     // Catch:{ IOException -> 0x00e0 }
        L_0x00ce:
            java.lang.String r10 = r9.getPackageName()     // Catch:{ IOException -> 0x00e0 }
            r6 = r10
            java.lang.String r10 = "pkg"
            r5.startTag(r0, r10)     // Catch:{ IOException -> 0x00e0 }
            java.lang.String r10 = "n"
            r5.attribute(r0, r10, r6)     // Catch:{ IOException -> 0x00e0 }
            goto L_0x00e5
        L_0x00e0:
            r0 = move-exception
            r27 = r4
            goto L_0x029f
        L_0x00e5:
            java.lang.String r10 = "uid"
            r5.startTag(r0, r10)     // Catch:{ IOException -> 0x029c }
            java.lang.String r10 = "n"
            int r11 = r9.getUid()     // Catch:{ IOException -> 0x029c }
            java.lang.String r11 = java.lang.Integer.toString(r11)     // Catch:{ IOException -> 0x029c }
            r5.attribute(r0, r10, r11)     // Catch:{ IOException -> 0x029c }
            monitor-enter(r33)     // Catch:{ IOException -> 0x029c }
            int r10 = r9.getUid()     // Catch:{ all -> 0x026d }
            java.lang.String r11 = r9.getPackageName()     // Catch:{ all -> 0x026d }
            com.android.server.appop.AppOpsService$Ops r10 = r1.getOpsRawLocked(r10, r11, r7, r7)     // Catch:{ all -> 0x026d }
            if (r10 == 0) goto L_0x011c
            java.lang.String r11 = "p"
            boolean r12 = r10.isPrivileged     // Catch:{ all -> 0x0115 }
            java.lang.String r12 = java.lang.Boolean.toString(r12)     // Catch:{ all -> 0x0115 }
            r5.attribute(r0, r11, r12)     // Catch:{ all -> 0x0115 }
            goto L_0x0126
        L_0x0115:
            r0 = move-exception
            r27 = r4
            r31 = r6
            goto L_0x0272
        L_0x011c:
            java.lang.String r11 = "p"
            java.lang.String r12 = java.lang.Boolean.toString(r7)     // Catch:{ all -> 0x026d }
            r5.attribute(r0, r11, r12)     // Catch:{ all -> 0x026d }
        L_0x0126:
            monitor-exit(r33)     // Catch:{ all -> 0x026d }
            java.util.List r10 = r9.getOps()     // Catch:{ IOException -> 0x029c }
            r11 = r7
        L_0x012c:
            int r12 = r10.size()     // Catch:{ IOException -> 0x029c }
            if (r11 >= r12) goto L_0x0257
            java.lang.Object r12 = r10.get(r11)     // Catch:{ IOException -> 0x029c }
            android.app.AppOpsManager$OpEntry r12 = (android.app.AppOpsManager.OpEntry) r12     // Catch:{ IOException -> 0x029c }
            java.lang.String r13 = "op"
            r5.startTag(r0, r13)     // Catch:{ IOException -> 0x029c }
            java.lang.String r13 = "n"
            int r14 = r12.getOp()     // Catch:{ IOException -> 0x029c }
            java.lang.String r14 = java.lang.Integer.toString(r14)     // Catch:{ IOException -> 0x029c }
            r5.attribute(r0, r13, r14)     // Catch:{ IOException -> 0x029c }
            int r13 = r12.getMode()     // Catch:{ IOException -> 0x029c }
            int r14 = r12.getOp()     // Catch:{ IOException -> 0x029c }
            int r14 = android.app.AppOpsManager.opToDefaultMode(r14)     // Catch:{ IOException -> 0x029c }
            if (r13 == r14) goto L_0x0168
            java.lang.String r13 = "m"
            int r14 = r12.getMode()     // Catch:{ IOException -> 0x00e0 }
            java.lang.String r14 = java.lang.Integer.toString(r14)     // Catch:{ IOException -> 0x00e0 }
            r5.attribute(r0, r13, r14)     // Catch:{ IOException -> 0x00e0 }
        L_0x0168:
            android.util.LongSparseArray r13 = r12.collectKeys()     // Catch:{ IOException -> 0x029c }
            if (r13 == 0) goto L_0x0241
            int r14 = r13.size()     // Catch:{ IOException -> 0x029c }
            if (r14 > 0) goto L_0x017a
            r27 = r4
            r31 = r6
            goto L_0x0245
        L_0x017a:
            int r14 = r13.size()     // Catch:{ IOException -> 0x029c }
            r15 = 0
        L_0x017f:
            if (r15 >= r14) goto L_0x0235
            long r16 = r13.keyAt(r15)     // Catch:{ IOException -> 0x029c }
            int r18 = android.app.AppOpsManager.extractUidStateFromKey(r16)     // Catch:{ IOException -> 0x029c }
            r19 = r18
            int r18 = android.app.AppOpsManager.extractFlagsFromKey(r16)     // Catch:{ IOException -> 0x029c }
            r20 = r18
            r7 = r19
            r0 = r20
            long r20 = r12.getLastAccessTime(r7, r7, r0)     // Catch:{ IOException -> 0x029c }
            long r22 = r12.getLastRejectTime(r7, r7, r0)     // Catch:{ IOException -> 0x029c }
            long r24 = r12.getLastDuration(r7, r7, r0)     // Catch:{ IOException -> 0x029c }
            java.lang.String r26 = r12.getProxyPackageName(r7, r0)     // Catch:{ IOException -> 0x029c }
            r27 = r26
            int r26 = r12.getProxyUid(r7, r0)     // Catch:{ IOException -> 0x029c }
            r28 = 0
            int r30 = (r20 > r28 ? 1 : (r20 == r28 ? 0 : -1))
            if (r30 > 0) goto L_0x01c6
            int r30 = (r22 > r28 ? 1 : (r22 == r28 ? 0 : -1))
            if (r30 > 0) goto L_0x01c6
            int r30 = (r24 > r28 ? 1 : (r24 == r28 ? 0 : -1))
            if (r30 > 0) goto L_0x01c6
            r30 = r0
            r0 = r27
            if (r0 != 0) goto L_0x01ca
            if (r26 >= 0) goto L_0x01ca
            r27 = r4
            r31 = r6
            goto L_0x022b
        L_0x01c6:
            r30 = r0
            r0 = r27
        L_0x01ca:
            r27 = r4
            java.lang.String r4 = "st"
            r31 = r6
            r6 = 0
            r5.startTag(r6, r4)     // Catch:{ IOException -> 0x0298 }
            java.lang.String r4 = "n"
            java.lang.String r6 = java.lang.Long.toString(r16)     // Catch:{ IOException -> 0x0298 }
            r32 = r7
            r7 = 0
            r5.attribute(r7, r4, r6)     // Catch:{ IOException -> 0x0298 }
            int r4 = (r20 > r28 ? 1 : (r20 == r28 ? 0 : -1))
            if (r4 <= 0) goto L_0x01f1
            java.lang.String r4 = "t"
            java.lang.String r6 = java.lang.Long.toString(r20)     // Catch:{ IOException -> 0x0298 }
            r7 = 0
            r5.attribute(r7, r4, r6)     // Catch:{ IOException -> 0x0298 }
        L_0x01f1:
            int r4 = (r22 > r28 ? 1 : (r22 == r28 ? 0 : -1))
            if (r4 <= 0) goto L_0x0200
            java.lang.String r4 = "r"
            java.lang.String r6 = java.lang.Long.toString(r22)     // Catch:{ IOException -> 0x0298 }
            r7 = 0
            r5.attribute(r7, r4, r6)     // Catch:{ IOException -> 0x0298 }
        L_0x0200:
            int r4 = (r24 > r28 ? 1 : (r24 == r28 ? 0 : -1))
            if (r4 <= 0) goto L_0x020e
            java.lang.String r4 = "d"
            java.lang.String r6 = java.lang.Long.toString(r24)     // Catch:{ IOException -> 0x0298 }
            r7 = 0
            r5.attribute(r7, r4, r6)     // Catch:{ IOException -> 0x0298 }
        L_0x020e:
            if (r0 == 0) goto L_0x0217
            java.lang.String r4 = "pp"
            r6 = 0
            r5.attribute(r6, r4, r0)     // Catch:{ IOException -> 0x0298 }
        L_0x0217:
            if (r26 < 0) goto L_0x0224
            java.lang.String r4 = "pu"
            java.lang.String r6 = java.lang.Integer.toString(r26)     // Catch:{ IOException -> 0x0298 }
            r7 = 0
            r5.attribute(r7, r4, r6)     // Catch:{ IOException -> 0x0298 }
        L_0x0224:
            java.lang.String r4 = "st"
            r6 = 0
            r5.endTag(r6, r4)     // Catch:{ IOException -> 0x0298 }
        L_0x022b:
            int r15 = r15 + 1
            r4 = r27
            r6 = r31
            r0 = 0
            r7 = 0
            goto L_0x017f
        L_0x0235:
            r27 = r4
            r31 = r6
            java.lang.String r0 = "op"
            r4 = 0
            r5.endTag(r4, r0)     // Catch:{ IOException -> 0x0298 }
            goto L_0x024d
        L_0x0241:
            r27 = r4
            r31 = r6
        L_0x0245:
            java.lang.String r0 = "op"
            r4 = 0
            r5.endTag(r4, r0)     // Catch:{ IOException -> 0x0298 }
        L_0x024d:
            int r11 = r11 + 1
            r4 = r27
            r6 = r31
            r0 = 0
            r7 = 0
            goto L_0x012c
        L_0x0257:
            r27 = r4
            r31 = r6
            java.lang.String r0 = "uid"
            r4 = 0
            r5.endTag(r4, r0)     // Catch:{ IOException -> 0x0298 }
            int r8 = r8 + 1
            r4 = r27
            r6 = r31
            r0 = 0
            r7 = 0
            goto L_0x00b0
        L_0x026d:
            r0 = move-exception
            r27 = r4
            r31 = r6
        L_0x0272:
            monitor-exit(r33)     // Catch:{ all -> 0x0274 }
            throw r0     // Catch:{ IOException -> 0x0298 }
        L_0x0274:
            r0 = move-exception
            goto L_0x0272
        L_0x0276:
            r27 = r4
            if (r6 == 0) goto L_0x0284
            java.lang.String r0 = "pkg"
            r4 = 0
            r5.endTag(r4, r0)     // Catch:{ IOException -> 0x0298 }
            goto L_0x0284
        L_0x0282:
            r27 = r4
        L_0x0284:
            java.lang.String r0 = "app-ops"
            r4 = 0
            r5.endTag(r4, r0)     // Catch:{ IOException -> 0x0298 }
            r5.endDocument()     // Catch:{ IOException -> 0x0298 }
            android.util.AtomicFile r0 = r1.mFile     // Catch:{ IOException -> 0x0298 }
            r0.finishWrite(r3)     // Catch:{ IOException -> 0x0298 }
            goto L_0x02ab
        L_0x0293:
            r0 = move-exception
            r27 = r4
        L_0x0296:
            monitor-exit(r33)     // Catch:{ all -> 0x029a }
            throw r0     // Catch:{ IOException -> 0x0298 }
        L_0x0298:
            r0 = move-exception
            goto L_0x029f
        L_0x029a:
            r0 = move-exception
            goto L_0x0296
        L_0x029c:
            r0 = move-exception
            r27 = r4
        L_0x029f:
            java.lang.String r4 = "AppOps"
            java.lang.String r5 = "Failed to write state, restoring backup."
            android.util.Slog.w(r4, r5, r0)     // Catch:{ all -> 0x02ad }
            android.util.AtomicFile r4 = r1.mFile     // Catch:{ all -> 0x02ad }
            r4.failWrite(r3)     // Catch:{ all -> 0x02ad }
        L_0x02ab:
            monitor-exit(r2)     // Catch:{ all -> 0x02ad }
            return
        L_0x02ad:
            r0 = move-exception
            goto L_0x02c8
        L_0x02af:
            r0 = move-exception
            java.lang.String r3 = "AppOps"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ad }
            r4.<init>()     // Catch:{ all -> 0x02ad }
            java.lang.String r5 = "Failed to write state: "
            r4.append(r5)     // Catch:{ all -> 0x02ad }
            r4.append(r0)     // Catch:{ all -> 0x02ad }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x02ad }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x02ad }
            monitor-exit(r2)     // Catch:{ all -> 0x02ad }
            return
        L_0x02c8:
            monitor-exit(r2)     // Catch:{ all -> 0x02ad }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.writeState():void");
    }

    static class Shell extends ShellCommand {
        static final Binder sBinder = new Binder();
        final IAppOpsService mInterface;
        final AppOpsService mInternal;
        IBinder mToken;
        int mode;
        String modeStr;
        int nonpackageUid;
        int op;
        String opStr;
        String packageName;
        int packageUid;
        boolean targetsUid;
        int userId = 0;

        Shell(IAppOpsService iface, AppOpsService internal) {
            this.mInterface = iface;
            this.mInternal = internal;
            try {
                this.mToken = this.mInterface.getToken(sBinder);
            } catch (RemoteException e) {
            }
        }

        public int onCommand(String cmd) {
            return AppOpsService.onShellCommand(this, cmd);
        }

        public void onHelp() {
            AppOpsService.dumpCommandHelp(getOutPrintWriter());
        }

        /* access modifiers changed from: private */
        public static int strOpToOp(String op2, PrintWriter err) {
            try {
                return AppOpsManager.strOpToOp(op2);
            } catch (IllegalArgumentException e) {
                try {
                    return Integer.parseInt(op2);
                } catch (NumberFormatException e2) {
                    try {
                        return AppOpsManager.strDebugOpToOp(op2);
                    } catch (IllegalArgumentException e3) {
                        err.println("Error: " + e3.getMessage());
                        return -1;
                    }
                }
            }
        }

        static int strModeToMode(String modeStr2, PrintWriter err) {
            for (int i = AppOpsManager.MODE_NAMES.length - 1; i >= 0; i--) {
                if (AppOpsManager.MODE_NAMES[i].equals(modeStr2)) {
                    return i;
                }
            }
            try {
                return Integer.parseInt(modeStr2);
            } catch (NumberFormatException e) {
                err.println("Error: Mode " + modeStr2 + " is not valid");
                return -1;
            }
        }

        /* access modifiers changed from: package-private */
        public int parseUserOpMode(int defMode, PrintWriter err) throws RemoteException {
            this.userId = -2;
            this.opStr = null;
            this.modeStr = null;
            while (true) {
                String nextArg = getNextArg();
                String argument = nextArg;
                if (nextArg == null) {
                    break;
                } else if ("--user".equals(argument)) {
                    this.userId = UserHandle.parseUserArg(getNextArgRequired());
                } else if (this.opStr == null) {
                    this.opStr = argument;
                } else if (this.modeStr == null) {
                    this.modeStr = argument;
                    break;
                }
            }
            String str = this.opStr;
            if (str == null) {
                err.println("Error: Operation not specified.");
                return -1;
            }
            this.op = strOpToOp(str, err);
            if (this.op < 0) {
                return -1;
            }
            String str2 = this.modeStr;
            if (str2 != null) {
                int strModeToMode = strModeToMode(str2, err);
                this.mode = strModeToMode;
                if (strModeToMode < 0) {
                    return -1;
                }
                return 0;
            }
            this.mode = defMode;
            return 0;
        }

        /* access modifiers changed from: package-private */
        public int parseUserPackageOp(boolean reqOp, PrintWriter err) throws RemoteException {
            this.userId = -2;
            this.packageName = null;
            this.opStr = null;
            while (true) {
                String nextArg = getNextArg();
                String argument = nextArg;
                if (nextArg == null) {
                    break;
                } else if ("--user".equals(argument)) {
                    this.userId = UserHandle.parseUserArg(getNextArgRequired());
                } else if ("--uid".equals(argument)) {
                    this.targetsUid = true;
                } else if (this.packageName == null) {
                    this.packageName = argument;
                } else if (this.opStr == null) {
                    this.opStr = argument;
                    break;
                }
            }
            if (this.packageName == null) {
                err.println("Error: Package name not specified.");
                return -1;
            } else if (this.opStr != null || !reqOp) {
                String str = this.opStr;
                if (str != null) {
                    this.op = strOpToOp(str, err);
                    if (this.op < 0) {
                        return -1;
                    }
                } else {
                    this.op = -1;
                }
                if (this.userId == -2) {
                    this.userId = ActivityManager.getCurrentUser();
                }
                this.nonpackageUid = -1;
                try {
                    this.nonpackageUid = Integer.parseInt(this.packageName);
                } catch (NumberFormatException e) {
                }
                if (this.nonpackageUid == -1 && this.packageName.length() > 1 && this.packageName.charAt(0) == 'u' && this.packageName.indexOf(46) < 0) {
                    int i = 1;
                    while (i < this.packageName.length() && this.packageName.charAt(i) >= '0' && this.packageName.charAt(i) <= '9') {
                        i++;
                    }
                    if (i > 1 && i < this.packageName.length()) {
                        try {
                            int user = Integer.parseInt(this.packageName.substring(1, i));
                            char type = this.packageName.charAt(i);
                            int i2 = i + 1;
                            int startTypeVal = i2;
                            while (i2 < this.packageName.length() && this.packageName.charAt(i2) >= '0' && this.packageName.charAt(i2) <= '9') {
                                i2++;
                            }
                            if (i2 > startTypeVal) {
                                try {
                                    int typeVal = Integer.parseInt(this.packageName.substring(startTypeVal, i2));
                                    if (type == 'a') {
                                        this.nonpackageUid = UserHandle.getUid(user, typeVal + 10000);
                                    } else if (type == 's') {
                                        this.nonpackageUid = UserHandle.getUid(user, typeVal);
                                    }
                                } catch (NumberFormatException e2) {
                                }
                            }
                        } catch (NumberFormatException e3) {
                        }
                    }
                }
                if (this.nonpackageUid != -1) {
                    this.packageName = null;
                } else {
                    this.packageUid = AppOpsService.resolveUid(this.packageName);
                    if (this.packageUid < 0) {
                        this.packageUid = AppGlobals.getPackageManager().getPackageUid(this.packageName, 8192, this.userId);
                    }
                    if (this.packageUid < 0) {
                        err.println("Error: No UID for " + this.packageName + " in user " + this.userId);
                        return -1;
                    }
                }
                return 0;
            } else {
                err.println("Error: Operation not specified.");
                return -1;
            }
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.appop.AppOpsService$Shell r0 = new com.android.server.appop.AppOpsService$Shell
            r0.<init>(r8, r8)
            r1 = r8
            r2 = r9
            r3 = r10
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r14
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    static void dumpCommandHelp(PrintWriter pw) {
        pw.println("AppOps service (appops) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("  start [--user <USER_ID>] <PACKAGE | UID> <OP> ");
        pw.println("    Starts a given operation for a particular application.");
        pw.println("  stop [--user <USER_ID>] <PACKAGE | UID> <OP> ");
        pw.println("    Stops a given operation for a particular application.");
        pw.println("  set [--user <USER_ID>] <[--uid] PACKAGE | UID> <OP> <MODE>");
        pw.println("    Set the mode for a particular application and operation.");
        pw.println("  get [--user <USER_ID>] <PACKAGE | UID> [<OP>]");
        pw.println("    Return the mode for a particular application and optional operation.");
        pw.println("  query-op [--user <USER_ID>] <OP> [<MODE>]");
        pw.println("    Print all packages that currently have the given op in the given mode.");
        pw.println("  reset [--user <USER_ID>] [<PACKAGE>]");
        pw.println("    Reset the given application or all applications to default modes.");
        pw.println("  write-settings");
        pw.println("    Immediately write pending changes to storage.");
        pw.println("  read-settings");
        pw.println("    Read the last written settings, replacing current state in RAM.");
        pw.println("  options:");
        pw.println("    <PACKAGE> an Android package name or its UID if prefixed by --uid");
        pw.println("    <OP>      an AppOps operation.");
        pw.println("    <MODE>    one of allow, ignore, deny, or default");
        pw.println("    <USER_ID> the user id under which the package is installed. If --user is not");
        pw.println("              specified, the current user is assumed.");
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static int onShellCommand(com.android.server.appop.AppOpsService.Shell r20, java.lang.String r21) {
        /*
            r1 = r20
            r2 = r21
            if (r2 != 0) goto L_0x000b
            int r0 = r20.handleDefaultCommands(r21)
            return r0
        L_0x000b:
            java.io.PrintWriter r3 = r20.getOutPrintWriter()
            java.io.PrintWriter r4 = r20.getErrPrintWriter()
            r5 = -1
            int r0 = r21.hashCode()     // Catch:{ RemoteException -> 0x0396 }
            r6 = 1
            r7 = 0
            switch(r0) {
                case -1703718319: goto L_0x006a;
                case -1166702330: goto L_0x005f;
                case 102230: goto L_0x0055;
                case 113762: goto L_0x004a;
                case 3540994: goto L_0x003f;
                case 108404047: goto L_0x0034;
                case 109757538: goto L_0x0029;
                case 2085703290: goto L_0x001e;
                default: goto L_0x001d;
            }     // Catch:{ RemoteException -> 0x0396 }
        L_0x001d:
            goto L_0x0075
        L_0x001e:
            java.lang.String r0 = "read-settings"
            boolean r0 = r2.equals(r0)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x001d
            r0 = 5
            goto L_0x0076
        L_0x0029:
            java.lang.String r0 = "start"
            boolean r0 = r2.equals(r0)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x001d
            r0 = 6
            goto L_0x0076
        L_0x0034:
            java.lang.String r0 = "reset"
            boolean r0 = r2.equals(r0)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x001d
            r0 = 3
            goto L_0x0076
        L_0x003f:
            java.lang.String r0 = "stop"
            boolean r0 = r2.equals(r0)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x001d
            r0 = 7
            goto L_0x0076
        L_0x004a:
            java.lang.String r0 = "set"
            boolean r0 = r2.equals(r0)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x001d
            r0 = r7
            goto L_0x0076
        L_0x0055:
            java.lang.String r0 = "get"
            boolean r0 = r2.equals(r0)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x001d
            r0 = r6
            goto L_0x0076
        L_0x005f:
            java.lang.String r0 = "query-op"
            boolean r0 = r2.equals(r0)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x001d
            r0 = 2
            goto L_0x0076
        L_0x006a:
            java.lang.String r0 = "write-settings"
            boolean r0 = r2.equals(r0)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x001d
            r0 = 4
            goto L_0x0076
        L_0x0075:
            r0 = r5
        L_0x0076:
            switch(r0) {
                case 0: goto L_0x0337;
                case 1: goto L_0x01e7;
                case 2: goto L_0x017e;
                case 3: goto L_0x0110;
                case 4: goto L_0x00d9;
                case 5: goto L_0x00b4;
                case 6: goto L_0x0099;
                case 7: goto L_0x007f;
                default: goto L_0x0079;
            }     // Catch:{ RemoteException -> 0x0396 }
        L_0x0079:
            int r0 = r20.handleDefaultCommands(r21)     // Catch:{ RemoteException -> 0x0396 }
            goto L_0x0395
        L_0x007f:
            int r0 = r1.parseUserPackageOp(r6, r4)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 >= 0) goto L_0x0086
            return r0
        L_0x0086:
            java.lang.String r6 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            if (r6 == 0) goto L_0x0098
            com.android.internal.app.IAppOpsService r6 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            android.os.IBinder r8 = r1.mToken     // Catch:{ RemoteException -> 0x0396 }
            int r9 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            int r10 = r1.packageUid     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r11 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            r6.finishOperation(r8, r9, r10, r11)     // Catch:{ RemoteException -> 0x0396 }
            return r7
        L_0x0098:
            return r5
        L_0x0099:
            int r0 = r1.parseUserPackageOp(r6, r4)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 >= 0) goto L_0x00a0
            return r0
        L_0x00a0:
            java.lang.String r6 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            if (r6 == 0) goto L_0x00b3
            com.android.internal.app.IAppOpsService r8 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            android.os.IBinder r9 = r1.mToken     // Catch:{ RemoteException -> 0x0396 }
            int r10 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            int r11 = r1.packageUid     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r12 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            r13 = 1
            r8.startOperation(r9, r10, r11, r12, r13)     // Catch:{ RemoteException -> 0x0396 }
            return r7
        L_0x00b3:
            return r5
        L_0x00b4:
            com.android.server.appop.AppOpsService r0 = r1.mInternal     // Catch:{ RemoteException -> 0x0396 }
            int r6 = android.os.Binder.getCallingPid()     // Catch:{ RemoteException -> 0x0396 }
            int r8 = android.os.Binder.getCallingUid()     // Catch:{ RemoteException -> 0x0396 }
            r0.enforceManageAppOpsModes(r6, r8, r5)     // Catch:{ RemoteException -> 0x0396 }
            long r8 = android.os.Binder.clearCallingIdentity()     // Catch:{ RemoteException -> 0x0396 }
            com.android.server.appop.AppOpsService r0 = r1.mInternal     // Catch:{ all -> 0x00d4 }
            r0.readState()     // Catch:{ all -> 0x00d4 }
            java.lang.String r0 = "Last settings read."
            r3.println(r0)     // Catch:{ all -> 0x00d4 }
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ RemoteException -> 0x0396 }
            return r7
        L_0x00d4:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ RemoteException -> 0x0396 }
            throw r0     // Catch:{ RemoteException -> 0x0396 }
        L_0x00d9:
            com.android.server.appop.AppOpsService r0 = r1.mInternal     // Catch:{ RemoteException -> 0x0396 }
            int r6 = android.os.Binder.getCallingPid()     // Catch:{ RemoteException -> 0x0396 }
            int r8 = android.os.Binder.getCallingUid()     // Catch:{ RemoteException -> 0x0396 }
            r0.enforceManageAppOpsModes(r6, r8, r5)     // Catch:{ RemoteException -> 0x0396 }
            long r8 = android.os.Binder.clearCallingIdentity()     // Catch:{ RemoteException -> 0x0396 }
            com.android.server.appop.AppOpsService r6 = r1.mInternal     // Catch:{ all -> 0x010b }
            monitor-enter(r6)     // Catch:{ all -> 0x010b }
            com.android.server.appop.AppOpsService r0 = r1.mInternal     // Catch:{ all -> 0x0108 }
            android.os.Handler r0 = r0.mHandler     // Catch:{ all -> 0x0108 }
            com.android.server.appop.AppOpsService r10 = r1.mInternal     // Catch:{ all -> 0x0108 }
            java.lang.Runnable r10 = r10.mWriteRunner     // Catch:{ all -> 0x0108 }
            r0.removeCallbacks(r10)     // Catch:{ all -> 0x0108 }
            monitor-exit(r6)     // Catch:{ all -> 0x0108 }
            com.android.server.appop.AppOpsService r0 = r1.mInternal     // Catch:{ all -> 0x010b }
            r0.writeState()     // Catch:{ all -> 0x010b }
            java.lang.String r0 = "Current settings written."
            r3.println(r0)     // Catch:{ all -> 0x010b }
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ RemoteException -> 0x0396 }
            return r7
        L_0x0108:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0108 }
            throw r0     // Catch:{ all -> 0x010b }
        L_0x010b:
            r0 = move-exception
            android.os.Binder.restoreCallingIdentity(r8)     // Catch:{ RemoteException -> 0x0396 }
            throw r0     // Catch:{ RemoteException -> 0x0396 }
        L_0x0110:
            r0 = 0
            r6 = -2
            r8 = r6
        L_0x0113:
            java.lang.String r9 = r20.getNextArg()     // Catch:{ RemoteException -> 0x0396 }
            r10 = r9
            if (r9 == 0) goto L_0x0145
            java.lang.String r9 = "--user"
            boolean r9 = r9.equals(r10)     // Catch:{ RemoteException -> 0x0396 }
            if (r9 == 0) goto L_0x012c
            java.lang.String r9 = r20.getNextArgRequired()     // Catch:{ RemoteException -> 0x0396 }
            int r11 = android.os.UserHandle.parseUserArg(r9)     // Catch:{ RemoteException -> 0x0396 }
            r8 = r11
            goto L_0x0113
        L_0x012c:
            if (r0 != 0) goto L_0x0130
            r0 = r10
            goto L_0x0113
        L_0x0130:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0396 }
            r6.<init>()     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r7 = "Error: Unsupported argument: "
            r6.append(r7)     // Catch:{ RemoteException -> 0x0396 }
            r6.append(r10)     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r6 = r6.toString()     // Catch:{ RemoteException -> 0x0396 }
            r4.println(r6)     // Catch:{ RemoteException -> 0x0396 }
            return r5
        L_0x0145:
            if (r8 != r6) goto L_0x014c
            int r6 = android.app.ActivityManager.getCurrentUser()     // Catch:{ RemoteException -> 0x0396 }
            r8 = r6
        L_0x014c:
            com.android.internal.app.IAppOpsService r6 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            r6.resetAllModes(r8, r0)     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r6 = "Reset all modes for: "
            r3.print(r6)     // Catch:{ RemoteException -> 0x0396 }
            if (r8 != r5) goto L_0x015e
            java.lang.String r6 = "all users"
            r3.print(r6)     // Catch:{ RemoteException -> 0x0396 }
            goto L_0x0167
        L_0x015e:
            java.lang.String r6 = "user "
            r3.print(r6)     // Catch:{ RemoteException -> 0x0396 }
            r3.print(r8)     // Catch:{ RemoteException -> 0x0396 }
        L_0x0167:
            java.lang.String r6 = ", "
            r3.print(r6)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 != 0) goto L_0x0174
            java.lang.String r6 = "all packages"
            r3.println(r6)     // Catch:{ RemoteException -> 0x0396 }
            goto L_0x017d
        L_0x0174:
            java.lang.String r6 = "package "
            r3.print(r6)     // Catch:{ RemoteException -> 0x0396 }
            r3.println(r0)     // Catch:{ RemoteException -> 0x0396 }
        L_0x017d:
            return r7
        L_0x017e:
            int r0 = r1.parseUserOpMode(r6, r4)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 >= 0) goto L_0x0185
            return r0
        L_0x0185:
            com.android.internal.app.IAppOpsService r8 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            int[] r6 = new int[r6]     // Catch:{ RemoteException -> 0x0396 }
            int r9 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            r6[r7] = r9     // Catch:{ RemoteException -> 0x0396 }
            java.util.List r6 = r8.getPackagesForOps(r6)     // Catch:{ RemoteException -> 0x0396 }
            if (r6 == 0) goto L_0x01e1
            int r8 = r6.size()     // Catch:{ RemoteException -> 0x0396 }
            if (r8 > 0) goto L_0x019a
            goto L_0x01e1
        L_0x019a:
            r8 = r7
        L_0x019b:
            int r9 = r6.size()     // Catch:{ RemoteException -> 0x0396 }
            if (r8 >= r9) goto L_0x01e0
            java.lang.Object r9 = r6.get(r8)     // Catch:{ RemoteException -> 0x0396 }
            android.app.AppOpsManager$PackageOps r9 = (android.app.AppOpsManager.PackageOps) r9     // Catch:{ RemoteException -> 0x0396 }
            r10 = 0
            java.lang.Object r11 = r6.get(r8)     // Catch:{ RemoteException -> 0x0396 }
            android.app.AppOpsManager$PackageOps r11 = (android.app.AppOpsManager.PackageOps) r11     // Catch:{ RemoteException -> 0x0396 }
            java.util.List r11 = r11.getOps()     // Catch:{ RemoteException -> 0x0396 }
            r12 = r7
        L_0x01b3:
            int r13 = r11.size()     // Catch:{ RemoteException -> 0x0396 }
            if (r12 >= r13) goto L_0x01d4
            java.lang.Object r13 = r11.get(r12)     // Catch:{ RemoteException -> 0x0396 }
            android.app.AppOpsManager$OpEntry r13 = (android.app.AppOpsManager.OpEntry) r13     // Catch:{ RemoteException -> 0x0396 }
            int r14 = r13.getOp()     // Catch:{ RemoteException -> 0x0396 }
            int r15 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            if (r14 != r15) goto L_0x01d1
            int r14 = r13.getMode()     // Catch:{ RemoteException -> 0x0396 }
            int r15 = r1.mode     // Catch:{ RemoteException -> 0x0396 }
            if (r14 != r15) goto L_0x01d1
            r10 = 1
            goto L_0x01d4
        L_0x01d1:
            int r12 = r12 + 1
            goto L_0x01b3
        L_0x01d4:
            if (r10 == 0) goto L_0x01dd
            java.lang.String r12 = r9.getPackageName()     // Catch:{ RemoteException -> 0x0396 }
            r3.println(r12)     // Catch:{ RemoteException -> 0x0396 }
        L_0x01dd:
            int r8 = r8 + 1
            goto L_0x019b
        L_0x01e0:
            return r7
        L_0x01e1:
            java.lang.String r8 = "No operations."
            r3.println(r8)     // Catch:{ RemoteException -> 0x0396 }
            return r7
        L_0x01e7:
            int r0 = r1.parseUserPackageOp(r7, r4)     // Catch:{ RemoteException -> 0x0396 }
            if (r0 >= 0) goto L_0x01ee
            return r0
        L_0x01ee:
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ RemoteException -> 0x0396 }
            r8.<init>()     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r9 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            r10 = 0
            if (r9 == 0) goto L_0x022b
            com.android.internal.app.IAppOpsService r9 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            int r11 = r1.packageUid     // Catch:{ RemoteException -> 0x0396 }
            int r12 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            if (r12 == r5) goto L_0x0207
            int[] r12 = new int[r6]     // Catch:{ RemoteException -> 0x0396 }
            int r13 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            r12[r7] = r13     // Catch:{ RemoteException -> 0x0396 }
            goto L_0x0208
        L_0x0207:
            r12 = r10
        L_0x0208:
            java.util.List r9 = r9.getUidOps(r11, r12)     // Catch:{ RemoteException -> 0x0396 }
            if (r9 == 0) goto L_0x0211
            r8.addAll(r9)     // Catch:{ RemoteException -> 0x0396 }
        L_0x0211:
            com.android.internal.app.IAppOpsService r11 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            int r12 = r1.packageUid     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r13 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            int r14 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            if (r14 == r5) goto L_0x0221
            int[] r10 = new int[r6]     // Catch:{ RemoteException -> 0x0396 }
            int r6 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            r10[r7] = r6     // Catch:{ RemoteException -> 0x0396 }
        L_0x0221:
            java.util.List r6 = r11.getOpsForPackage(r12, r13, r10)     // Catch:{ RemoteException -> 0x0396 }
            if (r6 == 0) goto L_0x022a
            r8.addAll(r6)     // Catch:{ RemoteException -> 0x0396 }
        L_0x022a:
            goto L_0x023e
        L_0x022b:
            com.android.internal.app.IAppOpsService r9 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            int r11 = r1.nonpackageUid     // Catch:{ RemoteException -> 0x0396 }
            int r12 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            if (r12 == r5) goto L_0x0239
            int[] r10 = new int[r6]     // Catch:{ RemoteException -> 0x0396 }
            int r6 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            r10[r7] = r6     // Catch:{ RemoteException -> 0x0396 }
        L_0x0239:
            java.util.List r6 = r9.getUidOps(r11, r10)     // Catch:{ RemoteException -> 0x0396 }
            r8 = r6
        L_0x023e:
            if (r8 == 0) goto L_0x0306
            int r6 = r8.size()     // Catch:{ RemoteException -> 0x0396 }
            if (r6 > 0) goto L_0x024a
            r19 = r8
            goto L_0x0308
        L_0x024a:
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ RemoteException -> 0x0396 }
            r6 = r7
        L_0x024f:
            int r11 = r8.size()     // Catch:{ RemoteException -> 0x0396 }
            if (r6 >= r11) goto L_0x0304
            java.lang.Object r11 = r8.get(r6)     // Catch:{ RemoteException -> 0x0396 }
            android.app.AppOpsManager$PackageOps r11 = (android.app.AppOpsManager.PackageOps) r11     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r12 = r11.getPackageName()     // Catch:{ RemoteException -> 0x0396 }
            if (r12 != 0) goto L_0x0266
            java.lang.String r12 = "Uid mode: "
            r3.print(r12)     // Catch:{ RemoteException -> 0x0396 }
        L_0x0266:
            java.util.List r12 = r11.getOps()     // Catch:{ RemoteException -> 0x0396 }
            r13 = r7
        L_0x026b:
            int r14 = r12.size()     // Catch:{ RemoteException -> 0x0396 }
            if (r13 >= r14) goto L_0x02fd
            java.lang.Object r14 = r12.get(r13)     // Catch:{ RemoteException -> 0x0396 }
            android.app.AppOpsManager$OpEntry r14 = (android.app.AppOpsManager.OpEntry) r14     // Catch:{ RemoteException -> 0x0396 }
            int r15 = r14.getOp()     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r15 = android.app.AppOpsManager.opToName(r15)     // Catch:{ RemoteException -> 0x0396 }
            r3.print(r15)     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r15 = ": "
            r3.print(r15)     // Catch:{ RemoteException -> 0x0396 }
            int r15 = r14.getMode()     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r15 = android.app.AppOpsManager.modeToName(r15)     // Catch:{ RemoteException -> 0x0396 }
            r3.print(r15)     // Catch:{ RemoteException -> 0x0396 }
            long r15 = r14.getTime()     // Catch:{ RemoteException -> 0x0396 }
            r17 = 0
            int r15 = (r15 > r17 ? 1 : (r15 == r17 ? 0 : -1))
            if (r15 == 0) goto L_0x02b2
            java.lang.String r15 = "; time="
            r3.print(r15)     // Catch:{ RemoteException -> 0x0396 }
            long r15 = r14.getTime()     // Catch:{ RemoteException -> 0x0396 }
            r19 = r8
            long r7 = r9 - r15
            android.util.TimeUtils.formatDuration(r7, r3)     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r7 = " ago"
            r3.print(r7)     // Catch:{ RemoteException -> 0x0396 }
            goto L_0x02b4
        L_0x02b2:
            r19 = r8
        L_0x02b4:
            long r7 = r14.getRejectTime()     // Catch:{ RemoteException -> 0x0396 }
            int r7 = (r7 > r17 ? 1 : (r7 == r17 ? 0 : -1))
            if (r7 == 0) goto L_0x02cf
            java.lang.String r7 = "; rejectTime="
            r3.print(r7)     // Catch:{ RemoteException -> 0x0396 }
            long r7 = r14.getRejectTime()     // Catch:{ RemoteException -> 0x0396 }
            long r7 = r9 - r7
            android.util.TimeUtils.formatDuration(r7, r3)     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r7 = " ago"
            r3.print(r7)     // Catch:{ RemoteException -> 0x0396 }
        L_0x02cf:
            long r7 = r14.getDuration()     // Catch:{ RemoteException -> 0x0396 }
            r15 = -1
            int r7 = (r7 > r15 ? 1 : (r7 == r15 ? 0 : -1))
            if (r7 != 0) goto L_0x02df
            java.lang.String r7 = " (running)"
            r3.print(r7)     // Catch:{ RemoteException -> 0x0396 }
            goto L_0x02f3
        L_0x02df:
            long r7 = r14.getDuration()     // Catch:{ RemoteException -> 0x0396 }
            int r7 = (r7 > r17 ? 1 : (r7 == r17 ? 0 : -1))
            if (r7 == 0) goto L_0x02f3
            java.lang.String r7 = "; duration="
            r3.print(r7)     // Catch:{ RemoteException -> 0x0396 }
            long r7 = r14.getDuration()     // Catch:{ RemoteException -> 0x0396 }
            android.util.TimeUtils.formatDuration(r7, r3)     // Catch:{ RemoteException -> 0x0396 }
        L_0x02f3:
            r3.println()     // Catch:{ RemoteException -> 0x0396 }
            int r13 = r13 + 1
            r8 = r19
            r7 = 0
            goto L_0x026b
        L_0x02fd:
            r19 = r8
            int r6 = r6 + 1
            r7 = 0
            goto L_0x024f
        L_0x0304:
            r5 = 0
            return r5
        L_0x0306:
            r19 = r8
        L_0x0308:
            java.lang.String r6 = "No operations."
            r3.println(r6)     // Catch:{ RemoteException -> 0x0396 }
            int r6 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            if (r6 <= r5) goto L_0x0335
            int r6 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            r7 = 91
            if (r6 >= r7) goto L_0x0335
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0396 }
            r6.<init>()     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r7 = "Default mode: "
            r6.append(r7)     // Catch:{ RemoteException -> 0x0396 }
            int r7 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            int r7 = android.app.AppOpsManager.opToDefaultMode(r7)     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r7 = android.app.AppOpsManager.modeToName(r7)     // Catch:{ RemoteException -> 0x0396 }
            r6.append(r7)     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r6 = r6.toString()     // Catch:{ RemoteException -> 0x0396 }
            r3.println(r6)     // Catch:{ RemoteException -> 0x0396 }
        L_0x0335:
            r5 = 0
            return r5
        L_0x0337:
            int r0 = r1.parseUserPackageOp(r6, r4)     // Catch:{ RemoteException -> 0x0396 }
            r6 = r0
            if (r6 >= 0) goto L_0x033f
            return r6
        L_0x033f:
            java.lang.String r0 = r20.getNextArg()     // Catch:{ RemoteException -> 0x0396 }
            r7 = r0
            if (r7 != 0) goto L_0x034c
            java.lang.String r0 = "Error: Mode not specified."
            r4.println(r0)     // Catch:{ RemoteException -> 0x0396 }
            return r5
        L_0x034c:
            int r0 = com.android.server.appop.AppOpsService.Shell.strModeToMode(r7, r4)     // Catch:{ RemoteException -> 0x0396 }
            r8 = r0
            if (r8 >= 0) goto L_0x0354
            return r5
        L_0x0354:
            boolean r0 = r1.targetsUid     // Catch:{ RemoteException -> 0x0396 }
            if (r0 != 0) goto L_0x0368
            java.lang.String r0 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x0368
            com.android.internal.app.IAppOpsService r0 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            int r9 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            int r10 = r1.packageUid     // Catch:{ RemoteException -> 0x0396 }
            java.lang.String r11 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            r0.setMode(r9, r10, r11, r8)     // Catch:{ RemoteException -> 0x0396 }
            goto L_0x0393
        L_0x0368:
            boolean r0 = r1.targetsUid     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x038a
            java.lang.String r0 = r1.packageName     // Catch:{ RemoteException -> 0x0396 }
            if (r0 == 0) goto L_0x038a
            com.android.server.appop.AppOpsService r0 = r1.mInternal     // Catch:{ NameNotFoundException -> 0x0388 }
            android.content.Context r0 = r0.mContext     // Catch:{ NameNotFoundException -> 0x0388 }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException -> 0x0388 }
            java.lang.String r9 = r1.packageName     // Catch:{ NameNotFoundException -> 0x0388 }
            int r10 = r1.userId     // Catch:{ NameNotFoundException -> 0x0388 }
            int r0 = r0.getPackageUid(r9, r10)     // Catch:{ NameNotFoundException -> 0x0388 }
            com.android.internal.app.IAppOpsService r9 = r1.mInterface     // Catch:{ NameNotFoundException -> 0x0388 }
            int r10 = r1.op     // Catch:{ NameNotFoundException -> 0x0388 }
            r9.setUidMode(r10, r0, r8)     // Catch:{ NameNotFoundException -> 0x0388 }
            goto L_0x0393
        L_0x0388:
            r0 = move-exception
            return r5
        L_0x038a:
            com.android.internal.app.IAppOpsService r0 = r1.mInterface     // Catch:{ RemoteException -> 0x0396 }
            int r9 = r1.op     // Catch:{ RemoteException -> 0x0396 }
            int r10 = r1.nonpackageUid     // Catch:{ RemoteException -> 0x0396 }
            r0.setUidMode(r9, r10, r8)     // Catch:{ RemoteException -> 0x0396 }
        L_0x0393:
            r0 = 0
            return r0
        L_0x0395:
            return r0
        L_0x0396:
            r0 = move-exception
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Remote exception: "
            r6.append(r7)
            r6.append(r0)
            java.lang.String r6 = r6.toString()
            r3.println(r6)
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.onShellCommand(com.android.server.appop.AppOpsService$Shell, java.lang.String):int");
    }

    private void dumpHelp(PrintWriter pw) {
        pw.println("AppOps service (appops) dump options:");
        pw.println("  -h");
        pw.println("    Print this help text.");
        pw.println("  --op [OP]");
        pw.println("    Limit output to data associated with the given app op code.");
        pw.println("  --mode [MODE]");
        pw.println("    Limit output to data associated with the given app op mode.");
        pw.println("  --package [PACKAGE]");
        pw.println("    Limit output to data associated with the given package name.");
        pw.println("  --watchers");
        pw.println("    Only output the watcher sections.");
    }

    private void dumpStatesLocked(PrintWriter pw, Op op, long now, SimpleDateFormat sdf, Date date, String prefix) {
        String str;
        String str2;
        PrintWriter printWriter = pw;
        Op op2 = op;
        Date date2 = date;
        String str3 = prefix;
        AppOpsManager.OpEntry entry = new AppOpsManager.OpEntry(op2.op, op2.running, op.mode, op.mAccessTimes, op.mRejectTimes, op.mDurations, op.mProxyUids, op.mProxyPackageNames);
        LongSparseArray keys = entry.collectKeys();
        if (keys == null) {
            AppOpsManager.OpEntry opEntry = entry;
            LongSparseArray longSparseArray = keys;
        } else if (keys.size() <= 0) {
            AppOpsManager.OpEntry opEntry2 = entry;
            LongSparseArray longSparseArray2 = keys;
        } else {
            int keyCount = keys.size();
            int proxyUid = 0;
            while (proxyUid < keyCount) {
                long key = keys.keyAt(proxyUid);
                int uidState = AppOpsManager.extractUidStateFromKey(key);
                int flags = AppOpsManager.extractFlagsFromKey(key);
                long accessTime = entry.getLastAccessTime(uidState, uidState, flags);
                long rejectTime = entry.getLastRejectTime(uidState, uidState, flags);
                LongSparseArray keys2 = keys;
                int keyCount2 = keyCount;
                long accessDuration = entry.getLastDuration(uidState, uidState, flags);
                String proxyPkg = entry.getProxyPackageName(uidState, flags);
                int k = proxyUid;
                int k2 = entry.getProxyUid(uidState, flags);
                AppOpsManager.OpEntry entry2 = entry;
                int i = uidState;
                int i2 = flags;
                long rejectTime2 = rejectTime;
                String str4 = "]";
                if (accessTime > 0) {
                    printWriter.print(str3);
                    printWriter.print("Access: ");
                    printWriter.print(AppOpsManager.keyToString(key));
                    printWriter.print(" ");
                    date2.setTime(accessTime);
                    printWriter.print(sdf.format(date));
                    printWriter.print(" (");
                    str = " (";
                    TimeUtils.formatDuration(accessTime - now, printWriter);
                    printWriter.print(")");
                    if (accessDuration > 0) {
                        printWriter.print(" duration=");
                        TimeUtils.formatDuration(accessDuration, printWriter);
                    }
                    if (k2 >= 0) {
                        printWriter.print(" proxy[");
                        printWriter.print("uid=");
                        printWriter.print(k2);
                        printWriter.print(", pkg=");
                        printWriter.print(proxyPkg);
                        str2 = str4;
                        printWriter.print(str2);
                    } else {
                        str2 = str4;
                    }
                    pw.println();
                } else {
                    str = " (";
                    str2 = str4;
                }
                if (rejectTime2 > 0) {
                    printWriter.print(prefix);
                    printWriter.print("Reject: ");
                    printWriter.print(AppOpsManager.keyToString(key));
                    long j = accessDuration;
                    long rejectTime3 = rejectTime2;
                    date2.setTime(rejectTime3);
                    printWriter.print(sdf.format(date));
                    printWriter.print(str);
                    long j2 = key;
                    TimeUtils.formatDuration(rejectTime3 - now, printWriter);
                    printWriter.print(")");
                    if (k2 >= 0) {
                        printWriter.print(" proxy[");
                        printWriter.print("uid=");
                        printWriter.print(k2);
                        printWriter.print(", pkg=");
                        printWriter.print(proxyPkg);
                        printWriter.print(str2);
                    }
                    pw.println();
                } else {
                    long j3 = rejectTime2;
                    long rejectTime4 = key;
                }
                proxyUid = k + 1;
                Op op3 = op;
                str3 = prefix;
                keys = keys2;
                keyCount = keyCount2;
                entry = entry2;
            }
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x035e, code lost:
        if (r1 != android.os.UserHandle.getAppId(r13.mWatchingUid)) goto L_0x0361;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:487:0x08c6, code lost:
        if (r3 != com.android.server.appop.AppOpsService.Op.access$100(r2)) goto L_0x08c9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:613:0x0c0b, code lost:
        if (r25 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:614:0x0c0d, code lost:
        if (r28 != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:615:0x0c0f, code lost:
        r9.mHistoricalRegistry.dump("  ", r38, r22, r11, r12);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:721:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:722:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:723:?, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:333:0x0682 A[Catch:{ all -> 0x0625 }] */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x0684 A[Catch:{ all -> 0x0625 }] */
    /* JADX WARNING: Removed duplicated region for block: B:336:0x0687 A[Catch:{ all -> 0x0625 }] */
    /* JADX WARNING: Removed duplicated region for block: B:337:0x068a A[Catch:{ all -> 0x0625 }] */
    /* JADX WARNING: Removed duplicated region for block: B:351:0x06b8  */
    /* JADX WARNING: Removed duplicated region for block: B:386:0x0733  */
    /* JADX WARNING: Removed duplicated region for block: B:394:0x0749  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dump(java.io.FileDescriptor r37, java.io.PrintWriter r38, java.lang.String[] r39) {
        /*
            r36 = this;
            r9 = r36
            r10 = r38
            r11 = r39
            android.content.Context r0 = r9.mContext
            java.lang.String r1 = "AppOps"
            boolean r0 = com.android.internal.util.DumpUtils.checkDumpAndUsageStatsPermission(r0, r1, r10)
            if (r0 != 0) goto L_0x0011
            return
        L_0x0011:
            r0 = -1
            r1 = 0
            r2 = -1
            r3 = -1
            r4 = 0
            r12 = 0
            r13 = 0
            r14 = 1
            if (r11 == 0) goto L_0x0101
            r5 = 0
            r6 = r4
            r4 = r3
            r3 = r2
            r2 = r0
        L_0x0020:
            int r0 = r11.length
            if (r5 >= r0) goto L_0x00fb
            r7 = r11[r5]
            java.lang.String r0 = "-h"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0031
            r9.dumpHelp(r10)
            return
        L_0x0031:
            java.lang.String r0 = "-a"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x003b
            goto L_0x00c0
        L_0x003b:
            java.lang.String r0 = "--op"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0059
            int r5 = r5 + 1
            int r0 = r11.length
            if (r5 < r0) goto L_0x004e
            java.lang.String r0 = "No argument for --op option"
            r10.println(r0)
            return
        L_0x004e:
            r0 = r11[r5]
            int r0 = com.android.server.appop.AppOpsService.Shell.strOpToOp(r0, r10)
            if (r0 >= 0) goto L_0x0057
            return
        L_0x0057:
            r2 = r0
            goto L_0x00c0
        L_0x0059:
            java.lang.String r0 = "--package"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0099
            int r5 = r5 + 1
            int r0 = r11.length
            if (r5 < r0) goto L_0x006c
            java.lang.String r0 = "No argument for --package option"
            r10.println(r0)
            return
        L_0x006c:
            r1 = r11[r5]
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x007b }
            r8 = 12591104(0xc02000, float:1.7643895E-38)
            int r0 = r0.getPackageUid(r1, r8, r13)     // Catch:{ RemoteException -> 0x007b }
            r3 = r0
            goto L_0x007c
        L_0x007b:
            r0 = move-exception
        L_0x007c:
            if (r3 >= 0) goto L_0x0093
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "Unknown package: "
            r0.append(r8)
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r10.println(r0)
            return
        L_0x0093:
            int r0 = android.os.UserHandle.getAppId(r3)
            r3 = r0
            goto L_0x00c0
        L_0x0099:
            java.lang.String r0 = "--mode"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00b7
            int r5 = r5 + 1
            int r0 = r11.length
            if (r5 < r0) goto L_0x00ac
            java.lang.String r0 = "No argument for --mode option"
            r10.println(r0)
            return
        L_0x00ac:
            r0 = r11[r5]
            int r0 = com.android.server.appop.AppOpsService.Shell.strModeToMode(r0, r10)
            if (r0 >= 0) goto L_0x00b5
            return
        L_0x00b5:
            r4 = r0
            goto L_0x00c0
        L_0x00b7:
            java.lang.String r0 = "--watchers"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00c3
            r6 = 1
        L_0x00c0:
            int r5 = r5 + r14
            goto L_0x0020
        L_0x00c3:
            int r0 = r7.length()
            if (r0 <= 0) goto L_0x00e6
            char r0 = r7.charAt(r13)
            r8 = 45
            if (r0 != r8) goto L_0x00e6
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "Unknown option: "
            r0.append(r8)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r10.println(r0)
            return
        L_0x00e6:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r8 = "Unknown command: "
            r0.append(r8)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r10.println(r0)
            return
        L_0x00fb:
            r8 = r2
            r15 = r6
            r2 = r1
            r1 = r3
            r3 = r4
            goto L_0x0108
        L_0x0101:
            r8 = r0
            r15 = r4
            r34 = r2
            r2 = r1
            r1 = r34
        L_0x0108:
            monitor-enter(r36)
            java.lang.String r0 = "Current AppOps Service state:"
            r10.println(r0)     // Catch:{ all -> 0x0c27 }
            if (r12 != 0) goto L_0x0125
            if (r15 != 0) goto L_0x0125
            com.android.server.appop.AppOpsService$Constants r0 = r9.mConstants     // Catch:{ all -> 0x0118 }
            r0.dump(r10)     // Catch:{ all -> 0x0118 }
            goto L_0x0125
        L_0x0118:
            r0 = move-exception
            r22 = r1
            r11 = r2
            r32 = r3
            r25 = r12
            r28 = r15
            r12 = r8
            goto L_0x0c32
        L_0x0125:
            r38.println()     // Catch:{ all -> 0x0c27 }
            long r4 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x0c27 }
            long r6 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0c27 }
            long r16 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x0c27 }
            java.text.SimpleDateFormat r0 = new java.text.SimpleDateFormat     // Catch:{ all -> 0x0c27 }
            java.lang.String r14 = "yyyy-MM-dd HH:mm:ss.SSS"
            r0.<init>(r14)     // Catch:{ all -> 0x0c27 }
            r18 = r6
            r6 = r0
            java.util.Date r7 = new java.util.Date     // Catch:{ all -> 0x0c27 }
            r7.<init>()     // Catch:{ all -> 0x0c27 }
            r0 = 0
            if (r8 >= 0) goto L_0x0187
            if (r3 >= 0) goto L_0x0187
            if (r2 != 0) goto L_0x0187
            android.util.SparseIntArray r14 = r9.mProfileOwners     // Catch:{ all -> 0x0118 }
            if (r14 == 0) goto L_0x0187
            if (r15 != 0) goto L_0x0187
            if (r12 != 0) goto L_0x0187
            java.lang.String r14 = "  Profile owners:"
            r10.println(r14)     // Catch:{ all -> 0x0118 }
            r14 = r13
        L_0x0159:
            android.util.SparseIntArray r13 = r9.mProfileOwners     // Catch:{ all -> 0x0118 }
            int r13 = r13.size()     // Catch:{ all -> 0x0118 }
            if (r14 >= r13) goto L_0x0184
            java.lang.String r13 = "    User #"
            r10.print(r13)     // Catch:{ all -> 0x0118 }
            android.util.SparseIntArray r13 = r9.mProfileOwners     // Catch:{ all -> 0x0118 }
            int r13 = r13.keyAt(r14)     // Catch:{ all -> 0x0118 }
            r10.print(r13)     // Catch:{ all -> 0x0118 }
            java.lang.String r13 = ": "
            r10.print(r13)     // Catch:{ all -> 0x0118 }
            android.util.SparseIntArray r13 = r9.mProfileOwners     // Catch:{ all -> 0x0118 }
            int r13 = r13.valueAt(r14)     // Catch:{ all -> 0x0118 }
            android.os.UserHandle.formatUid(r10, r13)     // Catch:{ all -> 0x0118 }
            r38.println()     // Catch:{ all -> 0x0118 }
            int r14 = r14 + 1
            r13 = 0
            goto L_0x0159
        L_0x0184:
            r38.println()     // Catch:{ all -> 0x0118 }
        L_0x0187:
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r13 = r9.mOpModeWatchers     // Catch:{ all -> 0x0c27 }
            int r13 = r13.size()     // Catch:{ all -> 0x0c27 }
            if (r13 <= 0) goto L_0x022d
            if (r12 != 0) goto L_0x022d
            r13 = 0
            r14 = 0
            r21 = r14
            r14 = r0
            r0 = r21
        L_0x0198:
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r11 = r9.mOpModeWatchers     // Catch:{ all -> 0x0118 }
            int r11 = r11.size()     // Catch:{ all -> 0x0118 }
            if (r0 >= r11) goto L_0x022e
            if (r8 < 0) goto L_0x01ac
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r11 = r9.mOpModeWatchers     // Catch:{ all -> 0x0118 }
            int r11 = r11.keyAt(r0)     // Catch:{ all -> 0x0118 }
            if (r8 == r11) goto L_0x01ac
            goto L_0x0227
        L_0x01ac:
            r11 = 0
            r21 = r11
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r11 = r9.mOpModeWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r11 = r11.valueAt(r0)     // Catch:{ all -> 0x0118 }
            android.util.ArraySet r11 = (android.util.ArraySet) r11     // Catch:{ all -> 0x0118 }
            r20 = 0
            r22 = r20
            r34 = r22
            r22 = r13
            r13 = r34
        L_0x01c1:
            r23 = r14
            int r14 = r11.size()     // Catch:{ all -> 0x0118 }
            if (r13 >= r14) goto L_0x0221
            java.lang.Object r14 = r11.valueAt(r13)     // Catch:{ all -> 0x0118 }
            com.android.server.appop.AppOpsService$ModeCallback r14 = (com.android.server.appop.AppOpsService.ModeCallback) r14     // Catch:{ all -> 0x0118 }
            if (r2 == 0) goto L_0x01de
            r24 = r11
            int r11 = r14.mWatchingUid     // Catch:{ all -> 0x0118 }
            int r11 = android.os.UserHandle.getAppId(r11)     // Catch:{ all -> 0x0118 }
            if (r1 == r11) goto L_0x01e0
            r14 = r23
            goto L_0x021c
        L_0x01de:
            r24 = r11
        L_0x01e0:
            r11 = 1
            if (r22 != 0) goto L_0x01ed
            r23 = r11
            java.lang.String r11 = "  Op mode watchers:"
            r10.println(r11)     // Catch:{ all -> 0x0118 }
            r22 = 1
            goto L_0x01ef
        L_0x01ed:
            r23 = r11
        L_0x01ef:
            if (r21 != 0) goto L_0x020a
            java.lang.String r11 = "    Op "
            r10.print(r11)     // Catch:{ all -> 0x0118 }
            android.util.SparseArray<android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r11 = r9.mOpModeWatchers     // Catch:{ all -> 0x0118 }
            int r11 = r11.keyAt(r0)     // Catch:{ all -> 0x0118 }
            java.lang.String r11 = android.app.AppOpsManager.opToName(r11)     // Catch:{ all -> 0x0118 }
            r10.print(r11)     // Catch:{ all -> 0x0118 }
            java.lang.String r11 = ":"
            r10.println(r11)     // Catch:{ all -> 0x0118 }
            r21 = 1
        L_0x020a:
            java.lang.String r11 = "      #"
            r10.print(r11)     // Catch:{ all -> 0x0118 }
            r10.print(r13)     // Catch:{ all -> 0x0118 }
            java.lang.String r11 = ": "
            r10.print(r11)     // Catch:{ all -> 0x0118 }
            r10.println(r14)     // Catch:{ all -> 0x0118 }
            r14 = r23
        L_0x021c:
            int r13 = r13 + 1
            r11 = r24
            goto L_0x01c1
        L_0x0221:
            r24 = r11
            r13 = r22
            r14 = r23
        L_0x0227:
            int r0 = r0 + 1
            r11 = r39
            goto L_0x0198
        L_0x022d:
            r14 = r0
        L_0x022e:
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r0 = r9.mPackageModeWatchers     // Catch:{ all -> 0x0c27 }
            int r0 = r0.size()     // Catch:{ all -> 0x0c27 }
            if (r0 <= 0) goto L_0x02aa
            if (r8 >= 0) goto L_0x02aa
            if (r12 != 0) goto L_0x02aa
            r0 = 0
            r11 = 0
            r13 = r11
        L_0x023d:
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r11 = r9.mPackageModeWatchers     // Catch:{ all -> 0x0118 }
            int r11 = r11.size()     // Catch:{ all -> 0x0118 }
            if (r13 >= r11) goto L_0x02aa
            if (r2 == 0) goto L_0x0254
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r11 = r9.mPackageModeWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r11 = r11.keyAt(r13)     // Catch:{ all -> 0x0118 }
            boolean r11 = r2.equals(r11)     // Catch:{ all -> 0x0118 }
            if (r11 != 0) goto L_0x0254
            goto L_0x02a7
        L_0x0254:
            r14 = 1
            if (r0 != 0) goto L_0x025d
            java.lang.String r11 = "  Package mode watchers:"
            r10.println(r11)     // Catch:{ all -> 0x0118 }
            r0 = 1
        L_0x025d:
            java.lang.String r11 = "    Pkg "
            r10.print(r11)     // Catch:{ all -> 0x0118 }
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r11 = r9.mPackageModeWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r11 = r11.keyAt(r13)     // Catch:{ all -> 0x0118 }
            java.lang.String r11 = (java.lang.String) r11     // Catch:{ all -> 0x0118 }
            r10.print(r11)     // Catch:{ all -> 0x0118 }
            java.lang.String r11 = ":"
            r10.println(r11)     // Catch:{ all -> 0x0118 }
            android.util.ArrayMap<java.lang.String, android.util.ArraySet<com.android.server.appop.AppOpsService$ModeCallback>> r11 = r9.mPackageModeWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r11 = r11.valueAt(r13)     // Catch:{ all -> 0x0118 }
            android.util.ArraySet r11 = (android.util.ArraySet) r11     // Catch:{ all -> 0x0118 }
            r20 = 0
            r21 = r20
            r22 = r0
            r0 = r21
        L_0x0282:
            r21 = r14
            int r14 = r11.size()     // Catch:{ all -> 0x0118 }
            if (r0 >= r14) goto L_0x02a3
            java.lang.String r14 = "      #"
            r10.print(r14)     // Catch:{ all -> 0x0118 }
            r10.print(r0)     // Catch:{ all -> 0x0118 }
            java.lang.String r14 = ": "
            r10.print(r14)     // Catch:{ all -> 0x0118 }
            java.lang.Object r14 = r11.valueAt(r0)     // Catch:{ all -> 0x0118 }
            r10.println(r14)     // Catch:{ all -> 0x0118 }
            int r0 = r0 + 1
            r14 = r21
            goto L_0x0282
        L_0x02a3:
            r14 = r21
            r0 = r22
        L_0x02a7:
            int r13 = r13 + 1
            goto L_0x023d
        L_0x02aa:
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ModeCallback> r0 = r9.mModeWatchers     // Catch:{ all -> 0x0c27 }
            int r0 = r0.size()     // Catch:{ all -> 0x0c27 }
            if (r0 <= 0) goto L_0x0311
            if (r8 >= 0) goto L_0x0311
            if (r12 != 0) goto L_0x0311
            r0 = 0
            r11 = 0
            r13 = r11
        L_0x02b9:
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ModeCallback> r11 = r9.mModeWatchers     // Catch:{ all -> 0x0118 }
            int r11 = r11.size()     // Catch:{ all -> 0x0118 }
            if (r13 >= r11) goto L_0x030d
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ModeCallback> r11 = r9.mModeWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r11 = r11.valueAt(r13)     // Catch:{ all -> 0x0118 }
            com.android.server.appop.AppOpsService$ModeCallback r11 = (com.android.server.appop.AppOpsService.ModeCallback) r11     // Catch:{ all -> 0x0118 }
            if (r2 == 0) goto L_0x02d8
            r21 = r14
            int r14 = r11.mWatchingUid     // Catch:{ all -> 0x0118 }
            int r14 = android.os.UserHandle.getAppId(r14)     // Catch:{ all -> 0x0118 }
            if (r1 == r14) goto L_0x02da
            r14 = r21
            goto L_0x030a
        L_0x02d8:
            r21 = r14
        L_0x02da:
            r14 = 1
            if (r0 != 0) goto L_0x02e6
            r22 = r0
            java.lang.String r0 = "  All op mode watchers:"
            r10.println(r0)     // Catch:{ all -> 0x0118 }
            r0 = 1
            goto L_0x02e8
        L_0x02e6:
            r22 = r0
        L_0x02e8:
            r21 = r0
            java.lang.String r0 = "    "
            r10.print(r0)     // Catch:{ all -> 0x0118 }
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ModeCallback> r0 = r9.mModeWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r0 = r0.keyAt(r13)     // Catch:{ all -> 0x0118 }
            int r0 = java.lang.System.identityHashCode(r0)     // Catch:{ all -> 0x0118 }
            java.lang.String r0 = java.lang.Integer.toHexString(r0)     // Catch:{ all -> 0x0118 }
            r10.print(r0)     // Catch:{ all -> 0x0118 }
            java.lang.String r0 = ": "
            r10.print(r0)     // Catch:{ all -> 0x0118 }
            r10.println(r11)     // Catch:{ all -> 0x0118 }
            r0 = r21
        L_0x030a:
            int r13 = r13 + 1
            goto L_0x02b9
        L_0x030d:
            r22 = r0
            r21 = r14
        L_0x0311:
            android.util.ArrayMap<android.os.IBinder, android.util.SparseArray<com.android.server.appop.AppOpsService$ActiveCallback>> r0 = r9.mActiveWatchers     // Catch:{ all -> 0x0c27 }
            int r0 = r0.size()     // Catch:{ all -> 0x0c27 }
            if (r0 <= 0) goto L_0x03d3
            if (r3 >= 0) goto L_0x03d3
            r14 = 1
            r0 = 0
            r20 = 0
            r21 = r20
            r34 = r21
            r21 = r0
            r0 = r34
        L_0x0327:
            android.util.ArrayMap<android.os.IBinder, android.util.SparseArray<com.android.server.appop.AppOpsService$ActiveCallback>> r11 = r9.mActiveWatchers     // Catch:{ all -> 0x0118 }
            int r11 = r11.size()     // Catch:{ all -> 0x0118 }
            if (r0 >= r11) goto L_0x03ce
            android.util.ArrayMap<android.os.IBinder, android.util.SparseArray<com.android.server.appop.AppOpsService$ActiveCallback>> r11 = r9.mActiveWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r11 = r11.valueAt(r0)     // Catch:{ all -> 0x0118 }
            android.util.SparseArray r11 = (android.util.SparseArray) r11     // Catch:{ all -> 0x0118 }
            int r23 = r11.size()     // Catch:{ all -> 0x0118 }
            if (r23 > 0) goto L_0x0340
            r24 = r14
            goto L_0x0361
        L_0x0340:
            r13 = 0
            java.lang.Object r24 = r11.valueAt(r13)     // Catch:{ all -> 0x0118 }
            com.android.server.appop.AppOpsService$ActiveCallback r24 = (com.android.server.appop.AppOpsService.ActiveCallback) r24     // Catch:{ all -> 0x0118 }
            r13 = r24
            if (r8 < 0) goto L_0x0354
            int r24 = r11.indexOfKey(r8)     // Catch:{ all -> 0x0118 }
            if (r24 >= 0) goto L_0x0354
            r24 = r14
            goto L_0x0361
        L_0x0354:
            if (r2 == 0) goto L_0x0364
            r24 = r14
            int r14 = r13.mWatchingUid     // Catch:{ all -> 0x0118 }
            int r14 = android.os.UserHandle.getAppId(r14)     // Catch:{ all -> 0x0118 }
            if (r1 == r14) goto L_0x0366
        L_0x0361:
            r26 = r4
            goto L_0x03c6
        L_0x0364:
            r24 = r14
        L_0x0366:
            if (r21 != 0) goto L_0x036f
            java.lang.String r14 = "  All op active watchers:"
            r10.println(r14)     // Catch:{ all -> 0x0118 }
            r21 = 1
        L_0x036f:
            java.lang.String r14 = "    "
            r10.print(r14)     // Catch:{ all -> 0x0118 }
            android.util.ArrayMap<android.os.IBinder, android.util.SparseArray<com.android.server.appop.AppOpsService$ActiveCallback>> r14 = r9.mActiveWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r14 = r14.keyAt(r0)     // Catch:{ all -> 0x0118 }
            int r14 = java.lang.System.identityHashCode(r14)     // Catch:{ all -> 0x0118 }
            java.lang.String r14 = java.lang.Integer.toHexString(r14)     // Catch:{ all -> 0x0118 }
            r10.print(r14)     // Catch:{ all -> 0x0118 }
            java.lang.String r14 = " ->"
            r10.println(r14)     // Catch:{ all -> 0x0118 }
            java.lang.String r14 = "        ["
            r10.print(r14)     // Catch:{ all -> 0x0118 }
            int r14 = r11.size()     // Catch:{ all -> 0x0118 }
            r25 = 0
            r26 = r4
            r4 = r25
        L_0x0399:
            if (r4 >= r14) goto L_0x03b9
            if (r4 <= 0) goto L_0x03a2
            r5 = 32
            r10.print(r5)     // Catch:{ all -> 0x0118 }
        L_0x03a2:
            int r5 = r11.keyAt(r4)     // Catch:{ all -> 0x0118 }
            java.lang.String r5 = android.app.AppOpsManager.opToName(r5)     // Catch:{ all -> 0x0118 }
            r10.print(r5)     // Catch:{ all -> 0x0118 }
            int r5 = r14 + -1
            if (r4 >= r5) goto L_0x03b6
            r5 = 44
            r10.print(r5)     // Catch:{ all -> 0x0118 }
        L_0x03b6:
            int r4 = r4 + 1
            goto L_0x0399
        L_0x03b9:
            java.lang.String r4 = "]"
            r10.println(r4)     // Catch:{ all -> 0x0118 }
            java.lang.String r4 = "        "
            r10.print(r4)     // Catch:{ all -> 0x0118 }
            r10.println(r13)     // Catch:{ all -> 0x0118 }
        L_0x03c6:
            int r0 = r0 + 1
            r14 = r24
            r4 = r26
            goto L_0x0327
        L_0x03ce:
            r26 = r4
            r24 = r14
            goto L_0x03d5
        L_0x03d3:
            r26 = r4
        L_0x03d5:
            android.util.ArrayMap<android.os.IBinder, android.util.SparseArray<com.android.server.appop.AppOpsService$NotedCallback>> r0 = r9.mNotedWatchers     // Catch:{ all -> 0x0c27 }
            int r0 = r0.size()     // Catch:{ all -> 0x0c27 }
            if (r0 <= 0) goto L_0x0488
            if (r3 >= 0) goto L_0x0488
            r14 = 1
            r0 = 0
            r4 = 0
            r5 = r4
        L_0x03e3:
            android.util.ArrayMap<android.os.IBinder, android.util.SparseArray<com.android.server.appop.AppOpsService$NotedCallback>> r4 = r9.mNotedWatchers     // Catch:{ all -> 0x0118 }
            int r4 = r4.size()     // Catch:{ all -> 0x0118 }
            if (r5 >= r4) goto L_0x0488
            android.util.ArrayMap<android.os.IBinder, android.util.SparseArray<com.android.server.appop.AppOpsService$NotedCallback>> r4 = r9.mNotedWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r4 = r4.valueAt(r5)     // Catch:{ all -> 0x0118 }
            android.util.SparseArray r4 = (android.util.SparseArray) r4     // Catch:{ all -> 0x0118 }
            int r11 = r4.size()     // Catch:{ all -> 0x0118 }
            if (r11 > 0) goto L_0x03fb
            goto L_0x0484
        L_0x03fb:
            r11 = 0
            java.lang.Object r13 = r4.valueAt(r11)     // Catch:{ all -> 0x0118 }
            com.android.server.appop.AppOpsService$NotedCallback r13 = (com.android.server.appop.AppOpsService.NotedCallback) r13     // Catch:{ all -> 0x0118 }
            r11 = r13
            if (r8 < 0) goto L_0x040d
            int r13 = r4.indexOfKey(r8)     // Catch:{ all -> 0x0118 }
            if (r13 >= 0) goto L_0x040d
            goto L_0x0484
        L_0x040d:
            if (r2 == 0) goto L_0x0418
            int r13 = r11.mWatchingUid     // Catch:{ all -> 0x0118 }
            int r13 = android.os.UserHandle.getAppId(r13)     // Catch:{ all -> 0x0118 }
            if (r1 == r13) goto L_0x0418
            goto L_0x0484
        L_0x0418:
            if (r0 != 0) goto L_0x0420
            java.lang.String r13 = "  All op noted watchers:"
            r10.println(r13)     // Catch:{ all -> 0x0118 }
            r0 = 1
        L_0x0420:
            java.lang.String r13 = "    "
            r10.print(r13)     // Catch:{ all -> 0x0118 }
            android.util.ArrayMap<android.os.IBinder, android.util.SparseArray<com.android.server.appop.AppOpsService$NotedCallback>> r13 = r9.mNotedWatchers     // Catch:{ all -> 0x0118 }
            java.lang.Object r13 = r13.keyAt(r5)     // Catch:{ all -> 0x0118 }
            int r13 = java.lang.System.identityHashCode(r13)     // Catch:{ all -> 0x0118 }
            java.lang.String r13 = java.lang.Integer.toHexString(r13)     // Catch:{ all -> 0x0118 }
            r10.print(r13)     // Catch:{ all -> 0x0118 }
            java.lang.String r13 = " ->"
            r10.println(r13)     // Catch:{ all -> 0x0118 }
            java.lang.String r13 = "        ["
            r10.print(r13)     // Catch:{ all -> 0x0118 }
            int r13 = r4.size()     // Catch:{ all -> 0x0118 }
            r5 = 0
        L_0x0445:
            if (r5 >= r13) goto L_0x0471
            if (r5 <= 0) goto L_0x0451
            r21 = r0
            r0 = 32
            r10.print(r0)     // Catch:{ all -> 0x0118 }
            goto L_0x0455
        L_0x0451:
            r21 = r0
            r0 = 32
        L_0x0455:
            int r23 = r4.keyAt(r5)     // Catch:{ all -> 0x0118 }
            java.lang.String r0 = android.app.AppOpsManager.opToName(r23)     // Catch:{ all -> 0x0118 }
            r10.print(r0)     // Catch:{ all -> 0x0118 }
            int r0 = r13 + -1
            if (r5 >= r0) goto L_0x046a
            r0 = 44
            r10.print(r0)     // Catch:{ all -> 0x0118 }
            goto L_0x046c
        L_0x046a:
            r0 = 44
        L_0x046c:
            int r5 = r5 + 1
            r0 = r21
            goto L_0x0445
        L_0x0471:
            r21 = r0
            r0 = 44
            java.lang.String r0 = "]"
            r10.println(r0)     // Catch:{ all -> 0x0118 }
            java.lang.String r0 = "        "
            r10.print(r0)     // Catch:{ all -> 0x0118 }
            r10.println(r11)     // Catch:{ all -> 0x0118 }
            r0 = r21
        L_0x0484:
            r4 = 1
            int r5 = r5 + r4
            goto L_0x03e3
        L_0x0488:
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ClientState> r0 = r9.mClients     // Catch:{ all -> 0x0c27 }
            int r0 = r0.size()     // Catch:{ all -> 0x0c27 }
            if (r0 <= 0) goto L_0x056a
            if (r3 >= 0) goto L_0x056a
            if (r15 != 0) goto L_0x056a
            if (r12 != 0) goto L_0x056a
            r14 = 1
            r0 = 0
            r4 = 0
            r5 = r4
        L_0x049a:
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ClientState> r4 = r9.mClients     // Catch:{ all -> 0x055d }
            int r4 = r4.size()     // Catch:{ all -> 0x055d }
            if (r5 >= r4) goto L_0x0558
            r4 = 0
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ClientState> r11 = r9.mClients     // Catch:{ all -> 0x055d }
            java.lang.Object r11 = r11.valueAt(r5)     // Catch:{ all -> 0x055d }
            com.android.server.appop.AppOpsService$ClientState r11 = (com.android.server.appop.AppOpsService.ClientState) r11     // Catch:{ all -> 0x055d }
            java.util.ArrayList<com.android.server.appop.AppOpsService$Op> r13 = r11.mStartedOps     // Catch:{ all -> 0x055d }
            int r13 = r13.size()     // Catch:{ all -> 0x055d }
            if (r13 <= 0) goto L_0x054c
            r13 = 0
            r20 = 0
            r21 = r20
            r34 = r21
            r21 = r0
            r0 = r34
        L_0x04be:
            r22 = r1
            java.util.ArrayList<com.android.server.appop.AppOpsService$Op> r1 = r11.mStartedOps     // Catch:{ all -> 0x0625 }
            int r1 = r1.size()     // Catch:{ all -> 0x0625 }
            if (r0 >= r1) goto L_0x0547
            java.util.ArrayList<com.android.server.appop.AppOpsService$Op> r1 = r11.mStartedOps     // Catch:{ all -> 0x0625 }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ all -> 0x0625 }
            com.android.server.appop.AppOpsService$Op r1 = (com.android.server.appop.AppOpsService.Op) r1     // Catch:{ all -> 0x0625 }
            if (r8 < 0) goto L_0x04d9
            r23 = r14
            int r14 = r1.op     // Catch:{ all -> 0x0625 }
            if (r14 == r8) goto L_0x04db
            goto L_0x053f
        L_0x04d9:
            r23 = r14
        L_0x04db:
            if (r2 == 0) goto L_0x04e6
            java.lang.String r14 = r1.packageName     // Catch:{ all -> 0x0625 }
            boolean r14 = r2.equals(r14)     // Catch:{ all -> 0x0625 }
            if (r14 != 0) goto L_0x04e6
            goto L_0x053f
        L_0x04e6:
            if (r21 != 0) goto L_0x04ef
            java.lang.String r14 = "  Clients:"
            r10.println(r14)     // Catch:{ all -> 0x0625 }
            r21 = 1
        L_0x04ef:
            if (r4 != 0) goto L_0x050d
            java.lang.String r14 = "    "
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ClientState> r14 = r9.mClients     // Catch:{ all -> 0x0625 }
            java.lang.Object r14 = r14.keyAt(r5)     // Catch:{ all -> 0x0625 }
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            java.lang.String r14 = ":"
            r10.println(r14)     // Catch:{ all -> 0x0625 }
            java.lang.String r14 = "      "
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            r10.println(r11)     // Catch:{ all -> 0x0625 }
            r4 = 1
        L_0x050d:
            if (r13 != 0) goto L_0x0515
            java.lang.String r14 = "      Started ops:"
            r10.println(r14)     // Catch:{ all -> 0x0625 }
            r13 = 1
        L_0x0515:
            java.lang.String r14 = "        "
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            java.lang.String r14 = "uid="
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            com.android.server.appop.AppOpsService$UidState r14 = r1.uidState     // Catch:{ all -> 0x0625 }
            int r14 = r14.uid     // Catch:{ all -> 0x0625 }
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            java.lang.String r14 = " pkg="
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            java.lang.String r14 = r1.packageName     // Catch:{ all -> 0x0625 }
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            java.lang.String r14 = " op="
            r10.print(r14)     // Catch:{ all -> 0x0625 }
            int r14 = r1.op     // Catch:{ all -> 0x0625 }
            java.lang.String r14 = android.app.AppOpsManager.opToName(r14)     // Catch:{ all -> 0x0625 }
            r10.println(r14)     // Catch:{ all -> 0x0625 }
        L_0x053f:
            int r0 = r0 + 1
            r1 = r22
            r14 = r23
            goto L_0x04be
        L_0x0547:
            r23 = r14
            r0 = r21
            goto L_0x0550
        L_0x054c:
            r22 = r1
            r23 = r14
        L_0x0550:
            int r5 = r5 + 1
            r1 = r22
            r14 = r23
            goto L_0x049a
        L_0x0558:
            r22 = r1
            r23 = r14
            goto L_0x056c
        L_0x055d:
            r0 = move-exception
            r22 = r1
            r11 = r2
            r32 = r3
            r25 = r12
            r28 = r15
            r12 = r8
            goto L_0x0c32
        L_0x056a:
            r22 = r1
        L_0x056c:
            android.util.SparseArray<android.util.SparseArray<com.android.server.appop.AppOpsService$Restriction>> r0 = r9.mAudioRestrictions     // Catch:{ all -> 0x0c1d }
            int r0 = r0.size()     // Catch:{ all -> 0x0c1d }
            if (r0 <= 0) goto L_0x061f
            if (r8 >= 0) goto L_0x061f
            if (r2 == 0) goto L_0x061f
            if (r3 >= 0) goto L_0x061f
            if (r15 != 0) goto L_0x061f
            if (r15 != 0) goto L_0x061f
            r0 = 0
            r1 = 0
            r4 = r1
        L_0x0581:
            android.util.SparseArray<android.util.SparseArray<com.android.server.appop.AppOpsService$Restriction>> r1 = r9.mAudioRestrictions     // Catch:{ all -> 0x0625 }
            int r1 = r1.size()     // Catch:{ all -> 0x0625 }
            if (r4 >= r1) goto L_0x061f
            android.util.SparseArray<android.util.SparseArray<com.android.server.appop.AppOpsService$Restriction>> r1 = r9.mAudioRestrictions     // Catch:{ all -> 0x0625 }
            int r1 = r1.keyAt(r4)     // Catch:{ all -> 0x0625 }
            java.lang.String r1 = android.app.AppOpsManager.opToName(r1)     // Catch:{ all -> 0x0625 }
            android.util.SparseArray<android.util.SparseArray<com.android.server.appop.AppOpsService$Restriction>> r5 = r9.mAudioRestrictions     // Catch:{ all -> 0x0625 }
            java.lang.Object r5 = r5.valueAt(r4)     // Catch:{ all -> 0x0625 }
            android.util.SparseArray r5 = (android.util.SparseArray) r5     // Catch:{ all -> 0x0625 }
            r11 = 0
            r13 = r11
        L_0x059d:
            int r11 = r5.size()     // Catch:{ all -> 0x0625 }
            if (r13 >= r11) goto L_0x0617
            if (r0 != 0) goto L_0x05ad
            java.lang.String r11 = "  Audio Restrictions:"
            r10.println(r11)     // Catch:{ all -> 0x0625 }
            r0 = 1
            r11 = 1
            r14 = r11
        L_0x05ad:
            int r11 = r5.keyAt(r13)     // Catch:{ all -> 0x0625 }
            r21 = r0
            java.lang.String r0 = "    "
            r10.print(r0)     // Catch:{ all -> 0x0625 }
            r10.print(r1)     // Catch:{ all -> 0x0625 }
            java.lang.String r0 = " usage="
            r10.print(r0)     // Catch:{ all -> 0x0625 }
            java.lang.String r0 = android.media.AudioAttributes.usageToString(r11)     // Catch:{ all -> 0x0625 }
            r10.print(r0)     // Catch:{ all -> 0x0625 }
            java.lang.Object r0 = r5.valueAt(r13)     // Catch:{ all -> 0x0625 }
            com.android.server.appop.AppOpsService$Restriction r0 = (com.android.server.appop.AppOpsService.Restriction) r0     // Catch:{ all -> 0x0625 }
            r23 = r1
            java.lang.String r1 = ": mode="
            r10.print(r1)     // Catch:{ all -> 0x0625 }
            int r1 = r0.mode     // Catch:{ all -> 0x0625 }
            java.lang.String r1 = android.app.AppOpsManager.modeToName(r1)     // Catch:{ all -> 0x0625 }
            r10.println(r1)     // Catch:{ all -> 0x0625 }
            android.util.ArraySet<java.lang.String> r1 = r0.exceptionPackages     // Catch:{ all -> 0x0625 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0625 }
            if (r1 != 0) goto L_0x060c
            java.lang.String r1 = "      Exceptions:"
            r10.println(r1)     // Catch:{ all -> 0x0625 }
            r1 = 0
            r24 = r1
        L_0x05ed:
            r24 = r5
            android.util.ArraySet<java.lang.String> r5 = r0.exceptionPackages     // Catch:{ all -> 0x0625 }
            int r5 = r5.size()     // Catch:{ all -> 0x0625 }
            if (r1 >= r5) goto L_0x060e
            java.lang.String r5 = "        "
            r10.print(r5)     // Catch:{ all -> 0x0625 }
            android.util.ArraySet<java.lang.String> r5 = r0.exceptionPackages     // Catch:{ all -> 0x0625 }
            java.lang.Object r5 = r5.valueAt(r1)     // Catch:{ all -> 0x0625 }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ all -> 0x0625 }
            r10.println(r5)     // Catch:{ all -> 0x0625 }
            int r1 = r1 + 1
            r5 = r24
            goto L_0x05ed
        L_0x060c:
            r24 = r5
        L_0x060e:
            int r13 = r13 + 1
            r0 = r21
            r1 = r23
            r5 = r24
            goto L_0x059d
        L_0x0617:
            r23 = r1
            r24 = r5
            int r4 = r4 + 1
            goto L_0x0581
        L_0x061f:
            if (r14 == 0) goto L_0x0630
            r38.println()     // Catch:{ all -> 0x0625 }
            goto L_0x0630
        L_0x0625:
            r0 = move-exception
            r11 = r2
            r32 = r3
            r25 = r12
            r28 = r15
            r12 = r8
            goto L_0x0c32
        L_0x0630:
            r1 = 0
            r0 = r1
        L_0x0632:
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r1 = r9.mUidStates     // Catch:{ all -> 0x0c1d }
            int r1 = r1.size()     // Catch:{ all -> 0x0c1d }
            if (r0 >= r1) goto L_0x0a2a
            android.util.SparseArray<com.android.server.appop.AppOpsService$UidState> r1 = r9.mUidStates     // Catch:{ all -> 0x0c1d }
            java.lang.Object r1 = r1.valueAt(r0)     // Catch:{ all -> 0x0c1d }
            com.android.server.appop.AppOpsService$UidState r1 = (com.android.server.appop.AppOpsService.UidState) r1     // Catch:{ all -> 0x0c1d }
            r11 = r1
            android.util.SparseIntArray r1 = r11.opModes     // Catch:{ all -> 0x0c1d }
            r13 = r1
            android.util.ArrayMap<java.lang.String, com.android.server.appop.AppOpsService$Ops> r1 = r11.pkgOps     // Catch:{ all -> 0x0c1d }
            r4 = r1
            if (r15 != 0) goto L_0x0a04
            if (r12 == 0) goto L_0x065f
            r11 = r2
            r32 = r3
            r25 = r12
            r29 = r14
            r28 = r15
            r4 = r26
            r12 = r8
            r26 = r18
            r18 = 0
            goto L_0x0a18
        L_0x065f:
            if (r8 >= 0) goto L_0x066e
            if (r2 != 0) goto L_0x066e
            if (r3 < 0) goto L_0x0666
            goto L_0x066e
        L_0x0666:
            r25 = r12
            r29 = r14
            r28 = r15
            goto L_0x075c
        L_0x066e:
            if (r8 < 0) goto L_0x067f
            android.util.SparseIntArray r1 = r11.opModes     // Catch:{ all -> 0x0625 }
            if (r1 == 0) goto L_0x067d
            android.util.SparseIntArray r1 = r11.opModes     // Catch:{ all -> 0x0625 }
            int r1 = r1.indexOfKey(r8)     // Catch:{ all -> 0x0625 }
            if (r1 < 0) goto L_0x067d
            goto L_0x067f
        L_0x067d:
            r1 = 0
            goto L_0x0680
        L_0x067f:
            r1 = 1
        L_0x0680:
            if (r2 != 0) goto L_0x0684
            r5 = 1
            goto L_0x0685
        L_0x0684:
            r5 = 0
        L_0x0685:
            if (r3 >= 0) goto L_0x068a
            r21 = 1
            goto L_0x068c
        L_0x068a:
            r21 = 0
        L_0x068c:
            if (r21 != 0) goto L_0x06b2
            if (r13 == 0) goto L_0x06b2
            r23 = 0
            r34 = r23
            r23 = r1
            r1 = r34
        L_0x0698:
            if (r21 != 0) goto L_0x06af
            r24 = r5
            int r5 = r13.size()     // Catch:{ all -> 0x0625 }
            if (r1 >= r5) goto L_0x06b6
            int r5 = r13.valueAt(r1)     // Catch:{ all -> 0x0625 }
            if (r5 != r3) goto L_0x06aa
            r21 = 1
        L_0x06aa:
            int r1 = r1 + 1
            r5 = r24
            goto L_0x0698
        L_0x06af:
            r24 = r5
            goto L_0x06b6
        L_0x06b2:
            r23 = r1
            r24 = r5
        L_0x06b6:
            if (r4 == 0) goto L_0x0733
            r1 = 0
            r25 = r12
            r5 = r24
            r12 = r1
            r1 = r23
        L_0x06c0:
            if (r1 == 0) goto L_0x06cc
            if (r5 == 0) goto L_0x06cc
            if (r21 != 0) goto L_0x06c7
            goto L_0x06cc
        L_0x06c7:
            r29 = r14
            r28 = r15
            goto L_0x0729
        L_0x06cc:
            r28 = r15
            int r15 = r4.size()     // Catch:{ all -> 0x072c }
            if (r12 >= r15) goto L_0x0727
            java.lang.Object r15 = r4.valueAt(r12)     // Catch:{ all -> 0x072c }
            com.android.server.appop.AppOpsService$Ops r15 = (com.android.server.appop.AppOpsService.Ops) r15     // Catch:{ all -> 0x072c }
            if (r1 != 0) goto L_0x06e5
            if (r15 == 0) goto L_0x06e5
            int r23 = r15.indexOfKey(r8)     // Catch:{ all -> 0x072c }
            if (r23 < 0) goto L_0x06e5
            r1 = 1
        L_0x06e5:
            if (r21 != 0) goto L_0x070f
            r23 = 0
            r34 = r23
            r23 = r1
            r1 = r34
        L_0x06ef:
            if (r21 != 0) goto L_0x070c
            r29 = r14
            int r14 = r15.size()     // Catch:{ all -> 0x072c }
            if (r1 >= r14) goto L_0x0713
            java.lang.Object r14 = r15.valueAt(r1)     // Catch:{ all -> 0x072c }
            com.android.server.appop.AppOpsService$Op r14 = (com.android.server.appop.AppOpsService.Op) r14     // Catch:{ all -> 0x072c }
            int r14 = r14.mode     // Catch:{ all -> 0x072c }
            if (r14 != r3) goto L_0x0707
            r21 = 1
        L_0x0707:
            int r1 = r1 + 1
            r14 = r29
            goto L_0x06ef
        L_0x070c:
            r29 = r14
            goto L_0x0713
        L_0x070f:
            r23 = r1
            r29 = r14
        L_0x0713:
            if (r5 != 0) goto L_0x071e
            java.lang.String r1 = r15.packageName     // Catch:{ all -> 0x072c }
            boolean r1 = r2.equals(r1)     // Catch:{ all -> 0x072c }
            if (r1 == 0) goto L_0x071e
            r5 = 1
        L_0x071e:
            int r12 = r12 + 1
            r1 = r23
            r15 = r28
            r14 = r29
            goto L_0x06c0
        L_0x0727:
            r29 = r14
        L_0x0729:
            r24 = r5
            goto L_0x073b
        L_0x072c:
            r0 = move-exception
            r11 = r2
            r32 = r3
            r12 = r8
            goto L_0x0c32
        L_0x0733:
            r25 = r12
            r29 = r14
            r28 = r15
            r1 = r23
        L_0x073b:
            android.util.SparseBooleanArray r5 = r11.foregroundOps     // Catch:{ all -> 0x09ee }
            if (r5 == 0) goto L_0x074a
            if (r1 != 0) goto L_0x074a
            android.util.SparseBooleanArray r5 = r11.foregroundOps     // Catch:{ all -> 0x072c }
            int r5 = r5.indexOfKey(r8)     // Catch:{ all -> 0x072c }
            if (r5 <= 0) goto L_0x074a
            r1 = 1
        L_0x074a:
            if (r1 == 0) goto L_0x09f5
            if (r24 == 0) goto L_0x09f5
            if (r21 != 0) goto L_0x075c
            r11 = r2
            r32 = r3
            r12 = r8
            r4 = r26
            r26 = r18
            r18 = 0
            goto L_0x0a18
        L_0x075c:
            java.lang.String r1 = "  Uid "
            r10.print(r1)     // Catch:{ all -> 0x09ee }
            int r1 = r11.uid     // Catch:{ all -> 0x09ee }
            android.os.UserHandle.formatUid(r10, r1)     // Catch:{ all -> 0x09ee }
            java.lang.String r1 = ":"
            r10.println(r1)     // Catch:{ all -> 0x09ee }
            java.lang.String r1 = "    state="
            r10.print(r1)     // Catch:{ all -> 0x09ee }
            int r1 = r11.state     // Catch:{ all -> 0x09ee }
            java.lang.String r1 = android.app.AppOpsManager.getUidStateName(r1)     // Catch:{ all -> 0x09ee }
            r10.println(r1)     // Catch:{ all -> 0x09ee }
            int r1 = r11.state     // Catch:{ all -> 0x09ee }
            int r5 = r11.pendingState     // Catch:{ all -> 0x09ee }
            if (r1 == r5) goto L_0x078d
            java.lang.String r1 = "    pendingState="
            r10.print(r1)     // Catch:{ all -> 0x072c }
            int r1 = r11.pendingState     // Catch:{ all -> 0x072c }
            java.lang.String r1 = android.app.AppOpsManager.getUidStateName(r1)     // Catch:{ all -> 0x072c }
            r10.println(r1)     // Catch:{ all -> 0x072c }
        L_0x078d:
            long r14 = r11.pendingStateCommitTime     // Catch:{ all -> 0x09ee }
            r23 = 0
            int r1 = (r14 > r23 ? 1 : (r14 == r23 ? 0 : -1))
            if (r1 == 0) goto L_0x07a6
            java.lang.String r1 = "    pendingStateCommitTime="
            r10.print(r1)     // Catch:{ all -> 0x072c }
            long r14 = r11.pendingStateCommitTime     // Catch:{ all -> 0x072c }
            r12 = r6
            r5 = r18
            android.util.TimeUtils.formatDuration(r14, r5, r10)     // Catch:{ all -> 0x072c }
            r38.println()     // Catch:{ all -> 0x072c }
            goto L_0x07a9
        L_0x07a6:
            r12 = r6
            r5 = r18
        L_0x07a9:
            int r1 = r11.startNesting     // Catch:{ all -> 0x09ee }
            if (r1 == 0) goto L_0x07b7
            java.lang.String r1 = "    startNesting="
            r10.print(r1)     // Catch:{ all -> 0x072c }
            int r1 = r11.startNesting     // Catch:{ all -> 0x072c }
            r10.println(r1)     // Catch:{ all -> 0x072c }
        L_0x07b7:
            android.util.SparseBooleanArray r1 = r11.foregroundOps     // Catch:{ all -> 0x09ee }
            if (r1 == 0) goto L_0x080e
            if (r3 < 0) goto L_0x07c0
            r1 = 4
            if (r3 != r1) goto L_0x080e
        L_0x07c0:
            java.lang.String r1 = "    foregroundOps:"
            r10.println(r1)     // Catch:{ all -> 0x072c }
            r1 = 0
            r14 = r1
        L_0x07c7:
            android.util.SparseBooleanArray r1 = r11.foregroundOps     // Catch:{ all -> 0x072c }
            int r1 = r1.size()     // Catch:{ all -> 0x072c }
            if (r14 >= r1) goto L_0x0804
            if (r8 < 0) goto L_0x07da
            android.util.SparseBooleanArray r1 = r11.foregroundOps     // Catch:{ all -> 0x072c }
            int r1 = r1.keyAt(r14)     // Catch:{ all -> 0x072c }
            if (r8 == r1) goto L_0x07da
            goto L_0x0801
        L_0x07da:
            java.lang.String r1 = "      "
            r10.print(r1)     // Catch:{ all -> 0x072c }
            android.util.SparseBooleanArray r1 = r11.foregroundOps     // Catch:{ all -> 0x072c }
            int r1 = r1.keyAt(r14)     // Catch:{ all -> 0x072c }
            java.lang.String r1 = android.app.AppOpsManager.opToName(r1)     // Catch:{ all -> 0x072c }
            r10.print(r1)     // Catch:{ all -> 0x072c }
            java.lang.String r1 = ": "
            r10.print(r1)     // Catch:{ all -> 0x072c }
            android.util.SparseBooleanArray r1 = r11.foregroundOps     // Catch:{ all -> 0x072c }
            boolean r1 = r1.valueAt(r14)     // Catch:{ all -> 0x072c }
            if (r1 == 0) goto L_0x07fc
            java.lang.String r1 = "WATCHER"
            goto L_0x07fe
        L_0x07fc:
            java.lang.String r1 = "SILENT"
        L_0x07fe:
            r10.println(r1)     // Catch:{ all -> 0x072c }
        L_0x0801:
            int r14 = r14 + 1
            goto L_0x07c7
        L_0x0804:
            java.lang.String r1 = "    hasForegroundWatchers="
            r10.print(r1)     // Catch:{ all -> 0x072c }
            boolean r1 = r11.hasForegroundWatchers     // Catch:{ all -> 0x072c }
            r10.println(r1)     // Catch:{ all -> 0x072c }
        L_0x080e:
            r14 = 1
            if (r13 == 0) goto L_0x0864
            int r1 = r13.size()     // Catch:{ all -> 0x072c }
            r15 = 0
        L_0x0816:
            if (r15 >= r1) goto L_0x085f
            int r18 = r13.keyAt(r15)     // Catch:{ all -> 0x072c }
            r19 = r18
            int r18 = r13.valueAt(r15)     // Catch:{ all -> 0x072c }
            r21 = r18
            if (r8 < 0) goto L_0x082f
            r18 = r1
            r1 = r19
            if (r8 == r1) goto L_0x0833
            r23 = r5
            goto L_0x0858
        L_0x082f:
            r18 = r1
            r1 = r19
        L_0x0833:
            if (r3 < 0) goto L_0x083c
            r23 = r5
            r5 = r21
            if (r3 == r5) goto L_0x0840
            goto L_0x0858
        L_0x083c:
            r23 = r5
            r5 = r21
        L_0x0840:
            java.lang.String r6 = "      "
            r10.print(r6)     // Catch:{ all -> 0x072c }
            java.lang.String r6 = android.app.AppOpsManager.opToName(r1)     // Catch:{ all -> 0x072c }
            r10.print(r6)     // Catch:{ all -> 0x072c }
            java.lang.String r6 = ": mode="
            r10.print(r6)     // Catch:{ all -> 0x072c }
            java.lang.String r6 = android.app.AppOpsManager.modeToName(r5)     // Catch:{ all -> 0x072c }
            r10.println(r6)     // Catch:{ all -> 0x072c }
        L_0x0858:
            int r15 = r15 + 1
            r1 = r18
            r5 = r23
            goto L_0x0816
        L_0x085f:
            r18 = r1
            r23 = r5
            goto L_0x0866
        L_0x0864:
            r23 = r5
        L_0x0866:
            if (r4 != 0) goto L_0x0875
            r11 = r2
            r32 = r3
            r6 = r12
            r4 = r26
            r18 = 0
            r12 = r8
            r26 = r23
            goto L_0x0a1a
        L_0x0875:
            r1 = 0
            r5 = r1
            r15 = r5
        L_0x0878:
            int r1 = r4.size()     // Catch:{ all -> 0x09ee }
            if (r15 >= r1) goto L_0x09de
            java.lang.Object r1 = r4.valueAt(r15)     // Catch:{ all -> 0x09ee }
            com.android.server.appop.AppOpsService$Ops r1 = (com.android.server.appop.AppOpsService.Ops) r1     // Catch:{ all -> 0x09ee }
            r5 = r1
            if (r2 == 0) goto L_0x08a0
            java.lang.String r1 = r5.packageName     // Catch:{ all -> 0x072c }
            boolean r1 = r2.equals(r1)     // Catch:{ all -> 0x072c }
            if (r1 != 0) goto L_0x08a0
            r32 = r3
            r20 = r4
            r31 = r11
            r6 = r12
            r4 = r26
            r18 = 0
            r11 = r2
            r12 = r8
            r26 = r23
            goto L_0x09c5
        L_0x08a0:
            r1 = 0
            r18 = 0
            r6 = r18
        L_0x08a5:
            r19 = r2
            int r2 = r5.size()     // Catch:{ all -> 0x09d6 }
            if (r6 >= r2) goto L_0x09b3
            java.lang.Object r2 = r5.valueAt(r6)     // Catch:{ all -> 0x09d6 }
            com.android.server.appop.AppOpsService$Op r2 = (com.android.server.appop.AppOpsService.Op) r2     // Catch:{ all -> 0x09d6 }
            r20 = r4
            int r4 = r2.op     // Catch:{ all -> 0x09d6 }
            if (r8 < 0) goto L_0x08be
            if (r8 == r4) goto L_0x08be
            r21 = r6
            goto L_0x08c9
        L_0x08be:
            if (r3 < 0) goto L_0x08e5
            r21 = r6
            int r6 = r2.mode     // Catch:{ all -> 0x08dd }
            if (r3 == r6) goto L_0x08e7
        L_0x08c9:
            r32 = r3
            r31 = r11
            r6 = r12
            r11 = r19
            r12 = r8
            r34 = r21
            r21 = r5
            r4 = r26
            r26 = r23
            r23 = r34
            goto L_0x099f
        L_0x08dd:
            r0 = move-exception
            r32 = r3
            r12 = r8
            r11 = r19
            goto L_0x0c32
        L_0x08e5:
            r21 = r6
        L_0x08e7:
            if (r1 != 0) goto L_0x08fc
            java.lang.String r6 = "    Package "
            r10.print(r6)     // Catch:{ all -> 0x08dd }
            java.lang.String r6 = r5.packageName     // Catch:{ all -> 0x08dd }
            r10.print(r6)     // Catch:{ all -> 0x08dd }
            java.lang.String r6 = ":"
            r10.println(r6)     // Catch:{ all -> 0x08dd }
            r1 = 1
            r29 = r1
            goto L_0x08fe
        L_0x08fc:
            r29 = r1
        L_0x08fe:
            java.lang.String r1 = "      "
            r10.print(r1)     // Catch:{ all -> 0x09d6 }
            java.lang.String r1 = android.app.AppOpsManager.opToName(r4)     // Catch:{ all -> 0x09d6 }
            r10.print(r1)     // Catch:{ all -> 0x09d6 }
            java.lang.String r1 = " ("
            r10.print(r1)     // Catch:{ all -> 0x09d6 }
            int r1 = r2.mode     // Catch:{ all -> 0x09d6 }
            java.lang.String r1 = android.app.AppOpsManager.modeToName(r1)     // Catch:{ all -> 0x09d6 }
            r10.print(r1)     // Catch:{ all -> 0x09d6 }
            int r1 = android.app.AppOpsManager.opToSwitch(r4)     // Catch:{ all -> 0x09d6 }
            r6 = r1
            if (r6 == r4) goto L_0x094d
            java.lang.String r1 = " / switch "
            r10.print(r1)     // Catch:{ all -> 0x08dd }
            java.lang.String r1 = android.app.AppOpsManager.opToName(r6)     // Catch:{ all -> 0x08dd }
            r10.print(r1)     // Catch:{ all -> 0x08dd }
            java.lang.Object r1 = r5.get(r6)     // Catch:{ all -> 0x08dd }
            com.android.server.appop.AppOpsService$Op r1 = (com.android.server.appop.AppOpsService.Op) r1     // Catch:{ all -> 0x08dd }
            if (r1 == 0) goto L_0x093a
            int r30 = r1.mode     // Catch:{ all -> 0x08dd }
            goto L_0x093e
        L_0x093a:
            int r30 = android.app.AppOpsManager.opToDefaultMode(r6)     // Catch:{ all -> 0x08dd }
        L_0x093e:
            r31 = r1
            java.lang.String r1 = "="
            r10.print(r1)     // Catch:{ all -> 0x08dd }
            java.lang.String r1 = android.app.AppOpsManager.modeToName(r30)     // Catch:{ all -> 0x08dd }
            r10.print(r1)     // Catch:{ all -> 0x08dd }
        L_0x094d:
            java.lang.String r1 = "): "
            r10.println(r1)     // Catch:{ all -> 0x09d6 }
            java.lang.String r30 = "          "
            r1 = r36
            r31 = r11
            r11 = r19
            r19 = r2
            r2 = r38
            r32 = r3
            r3 = r19
            r33 = r12
            r12 = r8
            r8 = r30
            r30 = r6
            r6 = r33
            r34 = r23
            r24 = r4
            r23 = r21
            r21 = r5
            r4 = r26
            r26 = r34
            r1.dumpStatesLocked(r2, r3, r4, r6, r7, r8)     // Catch:{ all -> 0x0c34 }
            r1 = r19
            boolean r2 = r1.running     // Catch:{ all -> 0x0c34 }
            if (r2 == 0) goto L_0x098f
            java.lang.String r2 = "          Running start at: "
            r10.print(r2)     // Catch:{ all -> 0x0c34 }
            long r2 = r1.startRealtime     // Catch:{ all -> 0x0c34 }
            long r2 = r26 - r2
            android.util.TimeUtils.formatDuration(r2, r10)     // Catch:{ all -> 0x0c34 }
            r38.println()     // Catch:{ all -> 0x0c34 }
        L_0x098f:
            int r2 = r1.startNesting     // Catch:{ all -> 0x0c34 }
            if (r2 == 0) goto L_0x099d
            java.lang.String r2 = "          startNesting="
            r10.print(r2)     // Catch:{ all -> 0x0c34 }
            int r2 = r1.startNesting     // Catch:{ all -> 0x0c34 }
            r10.println(r2)     // Catch:{ all -> 0x0c34 }
        L_0x099d:
            r1 = r29
        L_0x099f:
            int r2 = r23 + 1
            r8 = r12
            r23 = r26
            r3 = r32
            r26 = r4
            r12 = r6
            r4 = r20
            r5 = r21
            r6 = r2
            r2 = r11
            r11 = r31
            goto L_0x08a5
        L_0x09b3:
            r32 = r3
            r20 = r4
            r21 = r5
            r31 = r11
            r11 = r19
            r4 = r26
            r26 = r23
            r23 = r6
            r6 = r12
            r12 = r8
        L_0x09c5:
            int r15 = r15 + 1
            r2 = r11
            r8 = r12
            r23 = r26
            r11 = r31
            r3 = r32
            r26 = r4
            r12 = r6
            r4 = r20
            goto L_0x0878
        L_0x09d6:
            r0 = move-exception
            r32 = r3
            r12 = r8
            r11 = r19
            goto L_0x0c32
        L_0x09de:
            r32 = r3
            r20 = r4
            r31 = r11
            r6 = r12
            r4 = r26
            r18 = 0
            r11 = r2
            r12 = r8
            r26 = r23
            goto L_0x0a1a
        L_0x09ee:
            r0 = move-exception
            r11 = r2
            r32 = r3
            r12 = r8
            goto L_0x0c32
        L_0x09f5:
            r32 = r3
            r20 = r4
            r12 = r8
            r31 = r11
            r4 = r26
            r11 = r2
            r26 = r18
            r18 = 0
            goto L_0x0a18
        L_0x0a04:
            r32 = r3
            r20 = r4
            r31 = r11
            r25 = r12
            r29 = r14
            r28 = r15
            r4 = r26
            r11 = r2
            r12 = r8
            r26 = r18
            r18 = 0
        L_0x0a18:
            r14 = r29
        L_0x0a1a:
            int r0 = r0 + 1
            r2 = r11
            r8 = r12
            r12 = r25
            r18 = r26
            r15 = r28
            r3 = r32
            r26 = r4
            goto L_0x0632
        L_0x0a2a:
            r11 = r2
            r32 = r3
            r25 = r12
            r29 = r14
            r28 = r15
            r4 = r26
            r12 = r8
            r26 = r18
            r18 = 0
            if (r29 == 0) goto L_0x0a3f
            r38.println()     // Catch:{ all -> 0x0c34 }
        L_0x0a3f:
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ClientRestrictionState> r0 = r9.mOpUserRestrictions     // Catch:{ all -> 0x0c34 }
            int r0 = r0.size()     // Catch:{ all -> 0x0c34 }
            r1 = 0
        L_0x0a46:
            if (r1 >= r0) goto L_0x0c02
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ClientRestrictionState> r2 = r9.mOpUserRestrictions     // Catch:{ all -> 0x0c34 }
            java.lang.Object r2 = r2.keyAt(r1)     // Catch:{ all -> 0x0c34 }
            android.os.IBinder r2 = (android.os.IBinder) r2     // Catch:{ all -> 0x0c34 }
            android.util.ArrayMap<android.os.IBinder, com.android.server.appop.AppOpsService$ClientRestrictionState> r3 = r9.mOpUserRestrictions     // Catch:{ all -> 0x0c34 }
            java.lang.Object r3 = r3.valueAt(r1)     // Catch:{ all -> 0x0c34 }
            com.android.server.appop.AppOpsService$ClientRestrictionState r3 = (com.android.server.appop.AppOpsService.ClientRestrictionState) r3     // Catch:{ all -> 0x0c34 }
            r8 = 0
            if (r32 >= 0) goto L_0x0bec
            if (r28 != 0) goto L_0x0bec
            if (r25 == 0) goto L_0x0a69
            r19 = r0
            r20 = r4
            r33 = r6
            r24 = r7
            goto L_0x0bf6
        L_0x0a69:
            android.util.SparseArray<boolean[]> r13 = r3.perUserRestrictions     // Catch:{ all -> 0x0c34 }
            if (r13 == 0) goto L_0x0a74
            android.util.SparseArray<boolean[]> r13 = r3.perUserRestrictions     // Catch:{ all -> 0x0c34 }
            int r13 = r13.size()     // Catch:{ all -> 0x0c34 }
            goto L_0x0a76
        L_0x0a74:
            r13 = r18
        L_0x0a76:
            if (r13 <= 0) goto L_0x0b3d
            if (r11 != 0) goto L_0x0b3d
            r14 = 0
            r15 = 0
        L_0x0a7c:
            if (r15 >= r13) goto L_0x0b33
            r19 = r0
            android.util.SparseArray<boolean[]> r0 = r3.perUserRestrictions     // Catch:{ all -> 0x0c34 }
            int r0 = r0.keyAt(r15)     // Catch:{ all -> 0x0c34 }
            r20 = r4
            android.util.SparseArray<boolean[]> r4 = r3.perUserRestrictions     // Catch:{ all -> 0x0c34 }
            java.lang.Object r4 = r4.valueAt(r15)     // Catch:{ all -> 0x0c34 }
            boolean[] r4 = (boolean[]) r4     // Catch:{ all -> 0x0c34 }
            if (r4 != 0) goto L_0x0a93
            goto L_0x0a9c
        L_0x0a93:
            if (r12 < 0) goto L_0x0aa3
            int r5 = r4.length     // Catch:{ all -> 0x0c34 }
            if (r12 >= r5) goto L_0x0a9c
            boolean r5 = r4[r12]     // Catch:{ all -> 0x0c34 }
            if (r5 != 0) goto L_0x0aa3
        L_0x0a9c:
            r33 = r6
            r24 = r7
            r6 = 1
            goto L_0x0b27
        L_0x0aa3:
            if (r8 != 0) goto L_0x0ac2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0c34 }
            r5.<init>()     // Catch:{ all -> 0x0c34 }
            r33 = r6
            java.lang.String r6 = "  User restrictions for token "
            r5.append(r6)     // Catch:{ all -> 0x0c34 }
            r5.append(r2)     // Catch:{ all -> 0x0c34 }
            java.lang.String r6 = ":"
            r5.append(r6)     // Catch:{ all -> 0x0c34 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0c34 }
            r10.println(r5)     // Catch:{ all -> 0x0c34 }
            r8 = 1
            goto L_0x0ac4
        L_0x0ac2:
            r33 = r6
        L_0x0ac4:
            if (r14 != 0) goto L_0x0acc
            java.lang.String r5 = "      Restricted ops:"
            r10.println(r5)     // Catch:{ all -> 0x0c34 }
            r14 = 1
        L_0x0acc:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0c34 }
            r5.<init>()     // Catch:{ all -> 0x0c34 }
            java.lang.String r6 = "["
            r5.append(r6)     // Catch:{ all -> 0x0c34 }
            int r6 = r4.length     // Catch:{ all -> 0x0c34 }
            r23 = 0
            r24 = r7
            r7 = r23
        L_0x0add:
            if (r7 >= r6) goto L_0x0b07
            boolean r23 = r4[r7]     // Catch:{ all -> 0x0c34 }
            if (r23 == 0) goto L_0x0afb
            r23 = r4
            int r4 = r5.length()     // Catch:{ all -> 0x0c34 }
            r30 = r6
            r6 = 1
            if (r4 <= r6) goto L_0x0af3
            java.lang.String r4 = ", "
            r5.append(r4)     // Catch:{ all -> 0x0c34 }
        L_0x0af3:
            java.lang.String r4 = android.app.AppOpsManager.opToName(r7)     // Catch:{ all -> 0x0c34 }
            r5.append(r4)     // Catch:{ all -> 0x0c34 }
            goto L_0x0b00
        L_0x0afb:
            r23 = r4
            r30 = r6
            r6 = 1
        L_0x0b00:
            int r7 = r7 + 1
            r4 = r23
            r6 = r30
            goto L_0x0add
        L_0x0b07:
            r23 = r4
            r30 = r6
            r6 = 1
            java.lang.String r4 = "]"
            r5.append(r4)     // Catch:{ all -> 0x0c34 }
            java.lang.String r4 = "        "
            r10.print(r4)     // Catch:{ all -> 0x0c34 }
            java.lang.String r4 = "user: "
            r10.print(r4)     // Catch:{ all -> 0x0c34 }
            r10.print(r0)     // Catch:{ all -> 0x0c34 }
            java.lang.String r4 = " restricted ops: "
            r10.print(r4)     // Catch:{ all -> 0x0c34 }
            r10.println(r5)     // Catch:{ all -> 0x0c34 }
        L_0x0b27:
            int r15 = r15 + 1
            r0 = r19
            r4 = r20
            r7 = r24
            r6 = r33
            goto L_0x0a7c
        L_0x0b33:
            r19 = r0
            r20 = r4
            r33 = r6
            r24 = r7
            r6 = 1
            goto L_0x0b46
        L_0x0b3d:
            r19 = r0
            r20 = r4
            r33 = r6
            r24 = r7
            r6 = 1
        L_0x0b46:
            android.util.SparseArray<java.lang.String[]> r0 = r3.perUserExcludedPackages     // Catch:{ all -> 0x0c34 }
            if (r0 == 0) goto L_0x0b51
            android.util.SparseArray<java.lang.String[]> r0 = r3.perUserExcludedPackages     // Catch:{ all -> 0x0c34 }
            int r0 = r0.size()     // Catch:{ all -> 0x0c34 }
            goto L_0x0b53
        L_0x0b51:
            r0 = r18
        L_0x0b53:
            if (r0 <= 0) goto L_0x0be7
            if (r12 >= 0) goto L_0x0be7
            r4 = 0
            r5 = 0
        L_0x0b59:
            if (r5 >= r0) goto L_0x0be2
            android.util.SparseArray<java.lang.String[]> r7 = r3.perUserExcludedPackages     // Catch:{ all -> 0x0c34 }
            int r7 = r7.keyAt(r5)     // Catch:{ all -> 0x0c34 }
            android.util.SparseArray<java.lang.String[]> r14 = r3.perUserExcludedPackages     // Catch:{ all -> 0x0c34 }
            java.lang.Object r14 = r14.valueAt(r5)     // Catch:{ all -> 0x0c34 }
            java.lang.String[] r14 = (java.lang.String[]) r14     // Catch:{ all -> 0x0c34 }
            if (r14 != 0) goto L_0x0b71
            r23 = r0
            r30 = r3
            goto L_0x0bd9
        L_0x0b71:
            if (r11 == 0) goto L_0x0b93
            r15 = 0
            int r6 = r14.length     // Catch:{ all -> 0x0c34 }
            r23 = r0
            r0 = r18
        L_0x0b79:
            if (r0 >= r6) goto L_0x0b90
            r30 = r14[r0]     // Catch:{ all -> 0x0c34 }
            r31 = r30
            r30 = r3
            r3 = r31
            boolean r31 = r11.equals(r3)     // Catch:{ all -> 0x0c34 }
            if (r31 == 0) goto L_0x0b8b
            r15 = 1
            goto L_0x0b92
        L_0x0b8b:
            int r0 = r0 + 1
            r3 = r30
            goto L_0x0b79
        L_0x0b90:
            r30 = r3
        L_0x0b92:
            goto L_0x0b98
        L_0x0b93:
            r23 = r0
            r30 = r3
            r15 = 1
        L_0x0b98:
            if (r15 != 0) goto L_0x0b9b
            goto L_0x0bd9
        L_0x0b9b:
            if (r8 != 0) goto L_0x0bb7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x0c34 }
            r0.<init>()     // Catch:{ all -> 0x0c34 }
            java.lang.String r3 = "  User restrictions for token "
            r0.append(r3)     // Catch:{ all -> 0x0c34 }
            r0.append(r2)     // Catch:{ all -> 0x0c34 }
            java.lang.String r3 = ":"
            r0.append(r3)     // Catch:{ all -> 0x0c34 }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0c34 }
            r10.println(r0)     // Catch:{ all -> 0x0c34 }
            r8 = 1
        L_0x0bb7:
            if (r4 != 0) goto L_0x0bbf
            java.lang.String r0 = "      Excluded packages:"
            r10.println(r0)     // Catch:{ all -> 0x0c34 }
            r4 = 1
        L_0x0bbf:
            java.lang.String r0 = "        "
            r10.print(r0)     // Catch:{ all -> 0x0c34 }
            java.lang.String r0 = "user: "
            r10.print(r0)     // Catch:{ all -> 0x0c34 }
            r10.print(r7)     // Catch:{ all -> 0x0c34 }
            java.lang.String r0 = " packages: "
            r10.print(r0)     // Catch:{ all -> 0x0c34 }
            java.lang.String r0 = java.util.Arrays.toString(r14)     // Catch:{ all -> 0x0c34 }
            r10.println(r0)     // Catch:{ all -> 0x0c34 }
        L_0x0bd9:
            int r5 = r5 + 1
            r0 = r23
            r3 = r30
            r6 = 1
            goto L_0x0b59
        L_0x0be2:
            r23 = r0
            r30 = r3
            goto L_0x0bf6
        L_0x0be7:
            r23 = r0
            r30 = r3
            goto L_0x0bf6
        L_0x0bec:
            r19 = r0
            r30 = r3
            r20 = r4
            r33 = r6
            r24 = r7
        L_0x0bf6:
            int r1 = r1 + 1
            r0 = r19
            r4 = r20
            r7 = r24
            r6 = r33
            goto L_0x0a46
        L_0x0c02:
            r19 = r0
            r20 = r4
            r33 = r6
            r24 = r7
            monitor-exit(r36)     // Catch:{ all -> 0x0c34 }
            if (r25 == 0) goto L_0x0c1c
            if (r28 != 0) goto L_0x0c1c
            com.android.server.appop.HistoricalRegistry r1 = r9.mHistoricalRegistry
            java.lang.String r2 = "  "
            r3 = r38
            r4 = r22
            r5 = r11
            r6 = r12
            r1.dump(r2, r3, r4, r5, r6)
        L_0x0c1c:
            return
        L_0x0c1d:
            r0 = move-exception
            r11 = r2
            r32 = r3
            r25 = r12
            r28 = r15
            r12 = r8
            goto L_0x0c32
        L_0x0c27:
            r0 = move-exception
            r22 = r1
            r11 = r2
            r32 = r3
            r25 = r12
            r28 = r15
            r12 = r8
        L_0x0c32:
            monitor-exit(r36)     // Catch:{ all -> 0x0c34 }
            throw r0
        L_0x0c34:
            r0 = move-exception
            goto L_0x0c32
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.dump(java.io.FileDescriptor, java.io.PrintWriter, java.lang.String[]):void");
    }

    private static final class Restriction {
        private static final ArraySet<String> NO_EXCEPTIONS = new ArraySet<>();
        ArraySet<String> exceptionPackages;
        int mode;

        private Restriction() {
            this.exceptionPackages = NO_EXCEPTIONS;
        }
    }

    public void setUserRestrictions(Bundle restrictions, IBinder token, int userHandle) {
        checkSystemUid("setUserRestrictions");
        Preconditions.checkNotNull(restrictions);
        Preconditions.checkNotNull(token);
        for (int i = 0; i < 91; i++) {
            String restriction = AppOpsManager.opToRestriction(i);
            if (restriction != null) {
                setUserRestrictionNoCheck(i, restrictions.getBoolean(restriction, false), token, userHandle, (String[]) null);
            }
        }
    }

    public void setUserRestriction(int code, boolean restricted, IBinder token, int userHandle, String[] exceptionPackages) {
        if (Binder.getCallingPid() != Process.myPid()) {
            this.mContext.enforcePermission("android.permission.MANAGE_APP_OPS_RESTRICTIONS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        }
        if (userHandle == UserHandle.getCallingUserId() || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0 || this.mContext.checkCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS") == 0) {
            verifyIncomingOp(code);
            Preconditions.checkNotNull(token);
            setUserRestrictionNoCheck(code, restricted, token, userHandle, exceptionPackages);
            return;
        }
        throw new SecurityException("Need INTERACT_ACROSS_USERS_FULL or INTERACT_ACROSS_USERS to interact cross user ");
    }

    private void setUserRestrictionNoCheck(int code, boolean restricted, IBinder token, int userHandle, String[] exceptionPackages) {
        Slog.i(TAG, "setUserRestriction: " + code + " action: " + restricted + " from uid: " + Binder.getCallingUid() + " pid: " + Binder.getCallingPid());
        synchronized (this) {
            ClientRestrictionState restrictionState = this.mOpUserRestrictions.get(token);
            if (restrictionState == null) {
                try {
                    restrictionState = new ClientRestrictionState(token);
                    this.mOpUserRestrictions.put(token, restrictionState);
                } catch (RemoteException e) {
                    return;
                }
            }
            if (restrictionState.setRestriction(code, restricted, exceptionPackages, userHandle)) {
                this.mHandler.sendMessage(PooledLambda.obtainMessage($$Lambda$AppOpsService$GUeKjlbzT65s86vaxy5gvOajuhw.INSTANCE, this, Integer.valueOf(code), -2));
            }
            if (restrictionState.isDefault()) {
                this.mOpUserRestrictions.remove(token);
                restrictionState.destroy();
            }
        }
    }

    /* access modifiers changed from: private */
    public void notifyWatchersOfChange(int code, int uid) {
        synchronized (this) {
            ArraySet<ModeCallback> callbacks = this.mOpModeWatchers.get(code);
            if (callbacks != null) {
                ArraySet arraySet = new ArraySet(callbacks);
                notifyOpChanged((ArraySet<ModeCallback>) arraySet, code, uid, (String) null);
            }
        }
    }

    public void removeUser(int userHandle) throws RemoteException {
        checkSystemUid("removeUser");
        synchronized (this) {
            for (int i = this.mOpUserRestrictions.size() - 1; i >= 0; i--) {
                this.mOpUserRestrictions.valueAt(i).removeUser(userHandle);
            }
            removeUidsForUserLocked(userHandle);
        }
    }

    public boolean isOperationActive(int code, int uid, String packageName) {
        if (Binder.getCallingUid() != uid && this.mContext.checkCallingOrSelfPermission("android.permission.WATCH_APPOPS") != 0) {
            return false;
        }
        verifyIncomingOp(code);
        if (resolvePackageName(uid, packageName) == null) {
            return false;
        }
        synchronized (this) {
            for (int i = this.mClients.size() - 1; i >= 0; i--) {
                ClientState client = this.mClients.valueAt(i);
                for (int j = client.mStartedOps.size() - 1; j >= 0; j--) {
                    Op op = client.mStartedOps.get(j);
                    if (op.op == code && op.uidState.uid == uid) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public void setHistoryParameters(int mode, long baseSnapshotInterval, int compressionStep) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "setHistoryParameters");
        this.mHistoricalRegistry.setHistoryParameters(mode, baseSnapshotInterval, (long) compressionStep);
    }

    public void offsetHistory(long offsetMillis) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "offsetHistory");
        this.mHistoricalRegistry.offsetHistory(offsetMillis);
    }

    public void addHistoricalOps(AppOpsManager.HistoricalOps ops) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "addHistoricalOps");
        this.mHistoricalRegistry.addHistoricalOps(ops);
    }

    public void resetHistoryParameters() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "resetHistoryParameters");
        this.mHistoricalRegistry.resetHistoryParameters();
    }

    public void clearHistory() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_APPOPS", "clearHistory");
        this.mHistoricalRegistry.clearHistory();
    }

    private void removeUidsForUserLocked(int userHandle) {
        for (int i = this.mUidStates.size() - 1; i >= 0; i--) {
            if (UserHandle.getUserId(this.mUidStates.keyAt(i)) == userHandle) {
                this.mUidStates.removeAt(i);
            }
        }
    }

    private void checkSystemUid(String function) {
        if (Binder.getCallingUid() != 1000) {
            throw new SecurityException(function + " must by called by the system");
        }
    }

    private static String resolvePackageName(int uid, String packageName) {
        if (uid == 0) {
            return "root";
        }
        if (uid == 2000) {
            return NotificationShellCmd.NOTIFICATION_PACKAGE;
        }
        if (uid == 1013) {
            return "media";
        }
        if (uid == 1041) {
            return "audioserver";
        }
        if (uid == 1047) {
            return "cameraserver";
        }
        if (uid == 1000 && packageName == null) {
            return PackageManagerService.PLATFORM_PACKAGE_NAME;
        }
        return packageName;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int resolveUid(java.lang.String r7) {
        /*
            r0 = -1
            if (r7 != 0) goto L_0x0004
            return r0
        L_0x0004:
            int r1 = r7.hashCode()
            r2 = 0
            r3 = 4
            r4 = 3
            r5 = 2
            r6 = 1
            switch(r1) {
                case -31178072: goto L_0x003c;
                case 3506402: goto L_0x0031;
                case 103772132: goto L_0x0026;
                case 109403696: goto L_0x001b;
                case 1344606873: goto L_0x0011;
                default: goto L_0x0010;
            }
        L_0x0010:
            goto L_0x0046
        L_0x0011:
            java.lang.String r1 = "audioserver"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0010
            r1 = r4
            goto L_0x0047
        L_0x001b:
            java.lang.String r1 = "shell"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0010
            r1 = r6
            goto L_0x0047
        L_0x0026:
            java.lang.String r1 = "media"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0010
            r1 = r5
            goto L_0x0047
        L_0x0031:
            java.lang.String r1 = "root"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0010
            r1 = r2
            goto L_0x0047
        L_0x003c:
            java.lang.String r1 = "cameraserver"
            boolean r1 = r7.equals(r1)
            if (r1 == 0) goto L_0x0010
            r1 = r3
            goto L_0x0047
        L_0x0046:
            r1 = r0
        L_0x0047:
            if (r1 == 0) goto L_0x005e
            if (r1 == r6) goto L_0x005b
            if (r1 == r5) goto L_0x0058
            if (r1 == r4) goto L_0x0055
            if (r1 == r3) goto L_0x0052
            return r0
        L_0x0052:
            r0 = 1047(0x417, float:1.467E-42)
            return r0
        L_0x0055:
            r0 = 1041(0x411, float:1.459E-42)
            return r0
        L_0x0058:
            r0 = 1013(0x3f5, float:1.42E-42)
            return r0
        L_0x005b:
            r0 = 2000(0x7d0, float:2.803E-42)
            return r0
        L_0x005e:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.appop.AppOpsService.resolveUid(java.lang.String):int");
    }

    private static String[] getPackagesForUid(int uid) {
        String[] packageNames = null;
        if (AppGlobals.getPackageManager() != null) {
            try {
                packageNames = AppGlobals.getPackageManager().getPackagesForUid(uid);
            } catch (RemoteException e) {
            }
        }
        if (packageNames == null) {
            return EmptyArray.STRING;
        }
        return packageNames;
    }

    private final class ClientRestrictionState implements IBinder.DeathRecipient {
        SparseArray<String[]> perUserExcludedPackages;
        SparseArray<boolean[]> perUserRestrictions;
        private final IBinder token;

        public ClientRestrictionState(IBinder token2) throws RemoteException {
            token2.linkToDeath(this, 0);
            this.token = token2;
        }

        public boolean setRestriction(int code, boolean restricted, String[] excludedPackages, int userId) {
            int[] users;
            boolean changed = false;
            if (this.perUserRestrictions == null && restricted) {
                this.perUserRestrictions = new SparseArray<>();
            }
            if (userId == -1) {
                List<UserInfo> liveUsers = UserManager.get(AppOpsService.this.mContext).getUsers(false);
                users = new int[liveUsers.size()];
                for (int i = 0; i < liveUsers.size(); i++) {
                    users[i] = liveUsers.get(i).id;
                }
            } else {
                users = new int[]{userId};
            }
            if (this.perUserRestrictions != null) {
                for (int thisUserId : users) {
                    boolean[] userRestrictions = this.perUserRestrictions.get(thisUserId);
                    if (userRestrictions == null && restricted) {
                        userRestrictions = new boolean[91];
                        this.perUserRestrictions.put(thisUserId, userRestrictions);
                    }
                    if (!(userRestrictions == null || userRestrictions[code] == restricted)) {
                        userRestrictions[code] = restricted;
                        if (!restricted && isDefault(userRestrictions)) {
                            this.perUserRestrictions.remove(thisUserId);
                            userRestrictions = null;
                        }
                        changed = true;
                    }
                    if (userRestrictions != null) {
                        boolean noExcludedPackages = ArrayUtils.isEmpty(excludedPackages);
                        if (this.perUserExcludedPackages == null && !noExcludedPackages) {
                            this.perUserExcludedPackages = new SparseArray<>();
                        }
                        SparseArray<String[]> sparseArray = this.perUserExcludedPackages;
                        if (sparseArray != null && !Arrays.equals(excludedPackages, (Object[]) sparseArray.get(thisUserId))) {
                            if (noExcludedPackages) {
                                this.perUserExcludedPackages.remove(thisUserId);
                                if (this.perUserExcludedPackages.size() <= 0) {
                                    this.perUserExcludedPackages = null;
                                }
                            } else {
                                this.perUserExcludedPackages.put(thisUserId, excludedPackages);
                            }
                            changed = true;
                        }
                    }
                }
            }
            return changed;
        }

        public boolean hasRestriction(int restriction, String packageName, int userId) {
            boolean[] restrictions;
            String[] perUserExclusions;
            SparseArray<boolean[]> sparseArray = this.perUserRestrictions;
            if (sparseArray == null || (restrictions = sparseArray.get(userId)) == null || !restrictions[restriction]) {
                return false;
            }
            SparseArray<String[]> sparseArray2 = this.perUserExcludedPackages;
            if (sparseArray2 == null || (perUserExclusions = sparseArray2.get(userId)) == null) {
                return true;
            }
            return true ^ ArrayUtils.contains(perUserExclusions, packageName);
        }

        public void removeUser(int userId) {
            SparseArray<String[]> sparseArray = this.perUserExcludedPackages;
            if (sparseArray != null) {
                sparseArray.remove(userId);
                if (this.perUserExcludedPackages.size() <= 0) {
                    this.perUserExcludedPackages = null;
                }
            }
            SparseArray<boolean[]> sparseArray2 = this.perUserRestrictions;
            if (sparseArray2 != null) {
                sparseArray2.remove(userId);
                if (this.perUserRestrictions.size() <= 0) {
                    this.perUserRestrictions = null;
                }
            }
        }

        public boolean isDefault() {
            SparseArray<boolean[]> sparseArray = this.perUserRestrictions;
            return sparseArray == null || sparseArray.size() <= 0;
        }

        public void binderDied() {
            synchronized (AppOpsService.this) {
                AppOpsService.this.mOpUserRestrictions.remove(this.token);
                if (this.perUserRestrictions != null) {
                    int userCount = this.perUserRestrictions.size();
                    for (int i = 0; i < userCount; i++) {
                        boolean[] restrictions = this.perUserRestrictions.valueAt(i);
                        int restrictionCount = restrictions.length;
                        for (int j = 0; j < restrictionCount; j++) {
                            if (restrictions[j]) {
                                AppOpsService.this.mHandler.post(new Runnable(j) {
                                    private final /* synthetic */ int f$1;

                                    {
                                        this.f$1 = r2;
                                    }

                                    public final void run() {
                                        AppOpsService.ClientRestrictionState.this.lambda$binderDied$0$AppOpsService$ClientRestrictionState(this.f$1);
                                    }
                                });
                            }
                        }
                    }
                    destroy();
                }
            }
        }

        public /* synthetic */ void lambda$binderDied$0$AppOpsService$ClientRestrictionState(int changedCode) {
            AppOpsService.this.notifyWatchersOfChange(changedCode, -2);
        }

        public void destroy() {
            this.token.unlinkToDeath(this, 0);
        }

        private boolean isDefault(boolean[] array) {
            if (ArrayUtils.isEmpty(array)) {
                return true;
            }
            for (boolean value : array) {
                if (value) {
                    return false;
                }
            }
            return true;
        }
    }

    private final class AppOpsManagerInternalImpl extends AppOpsManagerInternal {
        private AppOpsManagerInternalImpl() {
        }

        public void setDeviceAndProfileOwners(SparseIntArray owners) {
            synchronized (AppOpsService.this) {
                AppOpsService.this.mProfileOwners = owners;
            }
        }

        public void setUidMode(int code, int uid, int mode) {
            AppOpsService.this.setUidMode(code, uid, mode);
        }

        public void setAllPkgModesToDefault(int code, int uid) {
            AppOpsService.this.setAllPkgModesToDefault(code, uid);
        }

        public int checkOperationUnchecked(int code, int uid, String packageName) {
            return AppOpsService.this.checkOperationUnchecked(code, uid, packageName, true, false);
        }
    }

    public int registerCallback(IBinder callback) {
        return this.mServiceState.registerCallback(callback);
    }

    public int checkOperationInternal(int code, int uid, String packageName) {
        return checkOperationInternal(code, uid, packageName, false);
    }

    public int noteOperationNotRecord(int code, int uid, String packageName) {
        verifyIncomingUid(uid);
        verifyIncomingOp(code);
        String resolvedPackageName = resolvePackageName(uid, packageName);
        if (resolvedPackageName == null) {
            return 1;
        }
        return noteOperationUnchecked(code, uid, resolvedPackageName, -1, (String) null, 33);
    }

    public int noteProxyOperationNotRecord(int code, int proxyUid, String proxyPackageName, int proxiedUid, String proxiedPackageName) {
        int proxyFlags;
        int proxiedFlags;
        int i = proxyUid;
        verifyIncomingUid(i);
        verifyIncomingOp(code);
        String resolveProxyPackageName = resolvePackageName(proxyUid, proxyPackageName);
        if (resolveProxyPackageName == null) {
            return 1;
        }
        boolean isProxyTrusted = this.mContext.checkPermission("android.permission.UPDATE_APP_OPS_STATS", -1, i) == 0;
        if (isProxyTrusted) {
            proxyFlags = 2;
        } else {
            proxyFlags = 4;
        }
        int proxyMode = noteOperationUnchecked(code, proxyUid, resolveProxyPackageName, -1, (String) null, proxyFlags | 32);
        if (proxyMode != 0) {
            int i2 = proxiedUid;
        } else if (Binder.getCallingUid() != proxiedUid) {
            String resolveProxiedPackageName = resolvePackageName(proxiedUid, proxiedPackageName);
            if (resolveProxiedPackageName == null) {
                return 1;
            }
            if (isProxyTrusted) {
                proxiedFlags = 8;
            } else {
                proxiedFlags = 16;
            }
            return noteOperationUnchecked(code, proxiedUid, resolveProxiedPackageName, proxyUid, resolveProxyPackageName, proxiedFlags | 32);
        }
        return proxyMode;
    }

    public void onAppPermFlagsModified(String permName, String packageName, int flagMask, int flagValues, int callingUid, int userId, boolean overridePolicy) {
        if (Binder.getCallingUid() == 1000) {
            this.mServiceState.onAppPermFlagsModified(permName, packageName, flagMask, flagValues, callingUid, userId, overridePolicy);
        } else {
            throw new SecurityException("Only SYSTEM_UID can be access to this method");
        }
    }

    public void onAppRuntimePermStateModified(String permName, String packageName, boolean granted, int callingUid, int userId, boolean overridePolicy) {
        if (Binder.getCallingUid() == 1000) {
            this.mServiceState.onAppRuntimePermStateModified(permName, packageName, granted, callingUid, userId, overridePolicy);
            return;
        }
        throw new SecurityException("Only SYSTEM_UID can be access to this method");
    }
}
