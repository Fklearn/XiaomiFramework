package com.android.server.trust;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.admin.DevicePolicyManager;
import android.app.trust.ITrustListener;
import android.app.trust.ITrustManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricSourceType;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.content.PackageMonitor;
import com.android.internal.util.DumpUtils;
import com.android.internal.widget.LockPatternUtils;
import com.android.server.SystemService;
import com.android.server.job.controllers.JobStatus;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TrustManagerService extends SystemService {
    static final boolean DEBUG = (Build.IS_DEBUGGABLE && Log.isLoggable(TAG, 2));
    private static final int MSG_CLEANUP_USER = 8;
    private static final int MSG_DISPATCH_UNLOCK_ATTEMPT = 3;
    private static final int MSG_DISPATCH_UNLOCK_LOCKOUT = 13;
    private static final int MSG_ENABLED_AGENTS_CHANGED = 4;
    private static final int MSG_FLUSH_TRUST_USUALLY_MANAGED = 10;
    private static final int MSG_KEYGUARD_SHOWING_CHANGED = 6;
    private static final int MSG_REFRESH_DEVICE_LOCKED_FOR_USER = 14;
    private static final int MSG_REGISTER_LISTENER = 1;
    private static final int MSG_SCHEDULE_TRUST_TIMEOUT = 15;
    private static final int MSG_START_USER = 7;
    private static final int MSG_STOP_USER = 12;
    private static final int MSG_SWITCH_USER = 9;
    private static final int MSG_UNLOCK_USER = 11;
    private static final int MSG_UNREGISTER_LISTENER = 2;
    private static final String PERMISSION_PROVIDE_AGENT = "android.permission.PROVIDE_TRUST_AGENT";
    private static final String TAG = "TrustManagerService";
    private static final Intent TRUST_AGENT_INTENT = new Intent("android.service.trust.TrustAgentService");
    private static final String TRUST_TIMEOUT_ALARM_TAG = "TrustManagerService.trustTimeoutForUser";
    private static final long TRUST_TIMEOUT_IN_MILLIS = 14400000;
    private static final int TRUST_USUALLY_MANAGED_FLUSH_DELAY = 120000;
    /* access modifiers changed from: private */
    public final ArraySet<AgentInfo> mActiveAgents = new ArraySet<>();
    private final ActivityManager mActivityManager;
    /* access modifiers changed from: private */
    public AlarmManager mAlarmManager;
    final TrustArchive mArchive = new TrustArchive();
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public int mCurrentUser = 0;
    /* access modifiers changed from: private */
    @GuardedBy({"mDeviceLockedForUser"})
    public final SparseBooleanArray mDeviceLockedForUser = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            SparseBooleanArray usuallyManaged;
            boolean z = false;
            switch (msg.what) {
                case 1:
                    TrustManagerService.this.addListener((ITrustListener) msg.obj);
                    return;
                case 2:
                    TrustManagerService.this.removeListener((ITrustListener) msg.obj);
                    return;
                case 3:
                    TrustManagerService trustManagerService = TrustManagerService.this;
                    if (msg.arg1 != 0) {
                        z = true;
                    }
                    trustManagerService.dispatchUnlockAttempt(z, msg.arg2);
                    return;
                case 4:
                    TrustManagerService.this.refreshAgentList(-1);
                    TrustManagerService.this.refreshDeviceLockedForUser(-1);
                    return;
                case 6:
                    TrustManagerService trustManagerService2 = TrustManagerService.this;
                    trustManagerService2.refreshDeviceLockedForUser(trustManagerService2.mCurrentUser);
                    return;
                case 7:
                case 8:
                case 11:
                    TrustManagerService.this.refreshAgentList(msg.arg1);
                    return;
                case 9:
                    int unused = TrustManagerService.this.mCurrentUser = msg.arg1;
                    TrustManagerService.this.mSettingsObserver.updateContentObserver();
                    TrustManagerService.this.refreshDeviceLockedForUser(-1);
                    return;
                case 10:
                    synchronized (TrustManagerService.this.mTrustUsuallyManagedForUser) {
                        usuallyManaged = TrustManagerService.this.mTrustUsuallyManagedForUser.clone();
                    }
                    for (int i = 0; i < usuallyManaged.size(); i++) {
                        int userId = usuallyManaged.keyAt(i);
                        boolean value = usuallyManaged.valueAt(i);
                        if (value != TrustManagerService.this.mLockPatternUtils.isTrustUsuallyManaged(userId)) {
                            TrustManagerService.this.mLockPatternUtils.setTrustUsuallyManaged(value, userId);
                        }
                    }
                    return;
                case 12:
                    TrustManagerService.this.setDeviceLockedForUser(msg.arg1, true);
                    return;
                case 13:
                    TrustManagerService.this.dispatchUnlockLockout(msg.arg1, msg.arg2);
                    return;
                case 14:
                    if (msg.arg2 == 1) {
                        TrustManagerService.this.updateTrust(msg.arg1, 0, true);
                    }
                    TrustManagerService.this.refreshDeviceLockedForUser(msg.arg1);
                    return;
                case 15:
                    TrustManagerService.this.handleScheduleTrustTimeout(msg.arg1, msg.arg2);
                    return;
                default:
                    return;
            }
        }
    };
    /* access modifiers changed from: private */
    public final LockPatternUtils mLockPatternUtils;
    private final PackageMonitor mPackageMonitor = new PackageMonitor() {
        public void onSomePackagesChanged() {
            TrustManagerService.this.refreshAgentList(-1);
        }

        public boolean onPackageChanged(String packageName, int uid, String[] components) {
            return true;
        }

        public void onPackageDisappeared(String packageName, int reason) {
            TrustManagerService.this.removeAgentsOfPackage(packageName);
        }
    };
    private final Receiver mReceiver = new Receiver();
    private final IBinder mService = new ITrustManager.Stub() {
        public void reportUnlockAttempt(boolean authenticated, int userId) throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.obtainMessage(3, authenticated, userId).sendToTarget();
        }

        public void reportUnlockLockout(int timeoutMs, int userId) throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.obtainMessage(13, timeoutMs, userId).sendToTarget();
        }

        public void reportEnabledTrustAgentsChanged(int userId) throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.removeMessages(4);
            TrustManagerService.this.mHandler.sendEmptyMessage(4);
        }

        public void reportKeyguardShowingChanged() throws RemoteException {
            enforceReportPermission();
            TrustManagerService.this.mHandler.removeMessages(6);
            TrustManagerService.this.mHandler.sendEmptyMessage(6);
            TrustManagerService.this.mHandler.runWithScissors($$Lambda$TrustManagerService$1$98HKBkgC1PLlz_Q1vJz1OJtw4c.INSTANCE, 0);
        }

        static /* synthetic */ void lambda$reportKeyguardShowingChanged$0() {
        }

        public void registerTrustListener(ITrustListener trustListener) throws RemoteException {
            enforceListenerPermission();
            TrustManagerService.this.mHandler.obtainMessage(1, trustListener).sendToTarget();
        }

        public void unregisterTrustListener(ITrustListener trustListener) throws RemoteException {
            enforceListenerPermission();
            TrustManagerService.this.mHandler.obtainMessage(2, trustListener).sendToTarget();
        }

        public boolean isDeviceLocked(int userId) throws RemoteException {
            int userId2 = ActivityManager.handleIncomingUser(getCallingPid(), getCallingUid(), userId, false, true, "isDeviceLocked", (String) null);
            long token = Binder.clearCallingIdentity();
            try {
                if (!TrustManagerService.this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userId2)) {
                    userId2 = TrustManagerService.this.resolveProfileParent(userId2);
                }
                return TrustManagerService.this.isDeviceLockedInner(userId2);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean isDeviceSecure(int userId) throws RemoteException {
            int userId2 = ActivityManager.handleIncomingUser(getCallingPid(), getCallingUid(), userId, false, true, "isDeviceSecure", (String) null);
            long token = Binder.clearCallingIdentity();
            try {
                if (!TrustManagerService.this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userId2)) {
                    userId2 = TrustManagerService.this.resolveProfileParent(userId2);
                }
                return TrustManagerService.this.mLockPatternUtils.isSecure(userId2);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        private void enforceReportPermission() {
            TrustManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_KEYGUARD_SECURE_STORAGE", "reporting trust events");
        }

        private void enforceListenerPermission() {
            TrustManagerService.this.mContext.enforceCallingPermission("android.permission.TRUST_LISTENER", "register trust listener");
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, final PrintWriter fout, String[] args) {
            if (DumpUtils.checkDumpPermission(TrustManagerService.this.mContext, TrustManagerService.TAG, fout)) {
                if (TrustManagerService.this.isSafeMode()) {
                    fout.println("disabled because the system is in safe mode.");
                } else if (!TrustManagerService.this.mTrustAgentsCanRun) {
                    fout.println("disabled because the third-party apps can't run yet.");
                } else {
                    final List<UserInfo> userInfos = TrustManagerService.this.mUserManager.getUsers(true);
                    TrustManagerService.this.mHandler.runWithScissors(new Runnable() {
                        public void run() {
                            fout.println("Trust manager state:");
                            for (UserInfo user : userInfos) {
                                AnonymousClass1.this.dumpUser(fout, user, user.id == TrustManagerService.this.mCurrentUser);
                            }
                        }
                    }, 1500);
                }
            }
        }

        /* access modifiers changed from: private */
        public void dumpUser(PrintWriter fout, UserInfo user, boolean isCurrent) {
            fout.printf(" User \"%s\" (id=%d, flags=%#x)", new Object[]{user.name, Integer.valueOf(user.id), Integer.valueOf(user.flags)});
            if (!user.supportsSwitchToByUser()) {
                fout.println("(managed profile)");
                fout.println("   disabled because switching to this user is not possible.");
                return;
            }
            if (isCurrent) {
                fout.print(" (current)");
            }
            fout.print(": trusted=" + dumpBool(TrustManagerService.this.aggregateIsTrusted(user.id)));
            fout.print(", trustManaged=" + dumpBool(TrustManagerService.this.aggregateIsTrustManaged(user.id)));
            fout.print(", deviceLocked=" + dumpBool(TrustManagerService.this.isDeviceLockedInner(user.id)));
            fout.print(", strongAuthRequired=" + dumpHex(TrustManagerService.this.mStrongAuthTracker.getStrongAuthForUser(user.id)));
            fout.println();
            fout.println("   Enabled agents:");
            boolean duplicateSimpleNames = false;
            ArraySet<String> simpleNames = new ArraySet<>();
            Iterator it = TrustManagerService.this.mActiveAgents.iterator();
            while (it.hasNext()) {
                AgentInfo info = (AgentInfo) it.next();
                if (info.userId == user.id) {
                    boolean trusted = info.agent.isTrusted();
                    fout.print("    ");
                    fout.println(info.component.flattenToShortString());
                    fout.print("     bound=" + dumpBool(info.agent.isBound()));
                    fout.print(", connected=" + dumpBool(info.agent.isConnected()));
                    fout.print(", managingTrust=" + dumpBool(info.agent.isManagingTrust()));
                    fout.print(", trusted=" + dumpBool(trusted));
                    fout.println();
                    if (trusted) {
                        fout.println("      message=\"" + info.agent.getMessage() + "\"");
                    }
                    if (!info.agent.isConnected()) {
                        String restartTime = TrustArchive.formatDuration(info.agent.getScheduledRestartUptimeMillis() - SystemClock.uptimeMillis());
                        fout.println("      restartScheduledAt=" + restartTime);
                    }
                    if (!simpleNames.add(TrustArchive.getSimpleName(info.component))) {
                        duplicateSimpleNames = true;
                    }
                }
            }
            fout.println("   Events:");
            TrustManagerService.this.mArchive.dump(fout, 50, user.id, "    ", duplicateSimpleNames);
            fout.println();
        }

        private String dumpBool(boolean b) {
            return b ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0";
        }

        private String dumpHex(int i) {
            return "0x" + Integer.toHexString(i);
        }

        /* Debug info: failed to restart local var, previous not found, register: 7 */
        public void setDeviceLockedForUser(int userId, boolean locked) {
            enforceReportPermission();
            long identity = Binder.clearCallingIdentity();
            try {
                if (TrustManagerService.this.mLockPatternUtils.isSeparateProfileChallengeEnabled(userId) && TrustManagerService.this.mLockPatternUtils.isSecure(userId)) {
                    synchronized (TrustManagerService.this.mDeviceLockedForUser) {
                        TrustManagerService.this.mDeviceLockedForUser.put(userId, locked);
                    }
                    if (locked) {
                        try {
                            ActivityManager.getService().notifyLockedProfile(userId);
                        } catch (RemoteException e) {
                        }
                    }
                    Intent lockIntent = new Intent("android.intent.action.DEVICE_LOCKED_CHANGED");
                    lockIntent.addFlags(1073741824);
                    lockIntent.putExtra("android.intent.extra.user_handle", userId);
                    TrustManagerService.this.mContext.sendBroadcastAsUser(lockIntent, UserHandle.SYSTEM, "android.permission.TRUST_LISTENER", (Bundle) null);
                }
                Binder.restoreCallingIdentity(identity);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(identity);
                throw th;
            }
        }

        public boolean isTrustUsuallyManaged(int userId) {
            TrustManagerService.this.mContext.enforceCallingPermission("android.permission.TRUST_LISTENER", "query trust state");
            return TrustManagerService.this.isTrustUsuallyManagedInternal(userId);
        }

        public void unlockedByBiometricForUser(int userId, BiometricSourceType biometricSource) {
            enforceReportPermission();
            synchronized (TrustManagerService.this.mUsersUnlockedByBiometric) {
                TrustManagerService.this.mUsersUnlockedByBiometric.put(userId, true);
            }
            TrustManagerService.this.mHandler.obtainMessage(14, userId, (int) TrustManagerService.this.mSettingsObserver.getTrustAgentsExtendUnlock()).sendToTarget();
        }

        public void clearAllBiometricRecognized(BiometricSourceType biometricSource) {
            enforceReportPermission();
            synchronized (TrustManagerService.this.mUsersUnlockedByBiometric) {
                TrustManagerService.this.mUsersUnlockedByBiometric.clear();
            }
            TrustManagerService.this.mHandler.obtainMessage(14, -1, 0).sendToTarget();
        }
    };
    /* access modifiers changed from: private */
    public final SettingsObserver mSettingsObserver;
    /* access modifiers changed from: private */
    public final StrongAuthTracker mStrongAuthTracker;
    /* access modifiers changed from: private */
    public boolean mTrustAgentsCanRun = false;
    private final ArrayList<ITrustListener> mTrustListeners = new ArrayList<>();
    /* access modifiers changed from: private */
    public final ArrayMap<Integer, TrustTimeoutAlarmListener> mTrustTimeoutAlarmListenerForUser = new ArrayMap<>();
    /* access modifiers changed from: private */
    @GuardedBy({"mTrustUsuallyManagedForUser"})
    public final SparseBooleanArray mTrustUsuallyManagedForUser = new SparseBooleanArray();
    /* access modifiers changed from: private */
    @GuardedBy({"mUserIsTrusted"})
    public final SparseBooleanArray mUserIsTrusted = new SparseBooleanArray();
    /* access modifiers changed from: private */
    public final UserManager mUserManager;
    /* access modifiers changed from: private */
    @GuardedBy({"mUsersUnlockedByBiometric"})
    public final SparseBooleanArray mUsersUnlockedByBiometric = new SparseBooleanArray();

    /* JADX WARNING: type inference failed for: r0v10, types: [com.android.server.trust.TrustManagerService$1, android.os.IBinder] */
    public TrustManagerService(Context context) {
        super(context);
        this.mContext = context;
        this.mUserManager = (UserManager) this.mContext.getSystemService("user");
        this.mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
        this.mLockPatternUtils = new LockPatternUtils(context);
        this.mStrongAuthTracker = new StrongAuthTracker(context);
        this.mAlarmManager = (AlarmManager) this.mContext.getSystemService("alarm");
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
    }

    public void onStart() {
        publishBinderService("trust", this.mService);
    }

    public void onBootPhase(int phase) {
        if (!isSafeMode()) {
            if (phase == 500) {
                this.mPackageMonitor.register(this.mContext, this.mHandler.getLooper(), UserHandle.ALL, true);
                this.mReceiver.register(this.mContext);
                this.mLockPatternUtils.registerStrongAuthTracker(this.mStrongAuthTracker);
            } else if (phase == 600) {
                this.mTrustAgentsCanRun = true;
                refreshAgentList(-1);
                refreshDeviceLockedForUser(-1);
            } else if (phase == 1000) {
                maybeEnableFactoryTrustAgents(this.mLockPatternUtils, 0);
            }
        }
    }

    private final class SettingsObserver extends ContentObserver {
        private final Uri LOCK_SCREEN_WHEN_TRUST_LOST = Settings.Secure.getUriFor("lock_screen_when_trust_lost");
        private final Uri TRUST_AGENTS_EXTEND_UNLOCK = Settings.Secure.getUriFor("trust_agents_extend_unlock");
        private final ContentResolver mContentResolver;
        private final boolean mIsAutomotive;
        private boolean mLockWhenTrustLost;
        private boolean mTrustAgentsExtendUnlock;

        SettingsObserver(Handler handler) {
            super(handler);
            this.mIsAutomotive = TrustManagerService.this.getContext().getPackageManager().hasSystemFeature("android.hardware.type.automotive");
            this.mContentResolver = TrustManagerService.this.getContext().getContentResolver();
            updateContentObserver();
        }

        /* access modifiers changed from: package-private */
        public void updateContentObserver() {
            this.mContentResolver.unregisterContentObserver(this);
            this.mContentResolver.registerContentObserver(this.TRUST_AGENTS_EXTEND_UNLOCK, false, this, TrustManagerService.this.mCurrentUser);
            this.mContentResolver.registerContentObserver(this.LOCK_SCREEN_WHEN_TRUST_LOST, false, this, TrustManagerService.this.mCurrentUser);
            onChange(true, this.TRUST_AGENTS_EXTEND_UNLOCK);
            onChange(true, this.LOCK_SCREEN_WHEN_TRUST_LOST);
        }

        public void onChange(boolean selfChange, Uri uri) {
            boolean z = true;
            if (this.TRUST_AGENTS_EXTEND_UNLOCK.equals(uri)) {
                if (Settings.Secure.getIntForUser(this.mContentResolver, "trust_agents_extend_unlock", (int) (this.mIsAutomotive ^ 1), TrustManagerService.this.mCurrentUser) == 0) {
                    z = false;
                }
                this.mTrustAgentsExtendUnlock = z;
            } else if (this.LOCK_SCREEN_WHEN_TRUST_LOST.equals(uri)) {
                if (Settings.Secure.getIntForUser(this.mContentResolver, "lock_screen_when_trust_lost", 0, TrustManagerService.this.mCurrentUser) == 0) {
                    z = false;
                }
                this.mLockWhenTrustLost = z;
            }
        }

        /* access modifiers changed from: package-private */
        public boolean getTrustAgentsExtendUnlock() {
            return this.mTrustAgentsExtendUnlock;
        }

        /* access modifiers changed from: package-private */
        public boolean getLockWhenTrustLost() {
            return this.mLockWhenTrustLost;
        }
    }

    /* access modifiers changed from: private */
    public void maybeLockScreen(int userId) {
        if (userId == this.mCurrentUser && this.mSettingsObserver.getLockWhenTrustLost()) {
            if (DEBUG) {
                Slog.d(TAG, "Locking device because trust was lost");
            }
            try {
                WindowManagerGlobal.getWindowManagerService().lockNow((Bundle) null);
            } catch (RemoteException e) {
                Slog.e(TAG, "Error locking screen when trust was lost");
            }
            TrustTimeoutAlarmListener alarm = this.mTrustTimeoutAlarmListenerForUser.get(Integer.valueOf(userId));
            if (alarm != null && this.mSettingsObserver.getTrustAgentsExtendUnlock()) {
                this.mAlarmManager.cancel(alarm);
                alarm.setQueued(false);
            }
        }
    }

    private void scheduleTrustTimeout(int userId, boolean override) {
        int shouldOverride = override;
        if (override) {
            shouldOverride = 1;
        }
        this.mHandler.obtainMessage(15, userId, (int) shouldOverride).sendToTarget();
    }

    /* access modifiers changed from: private */
    public void handleScheduleTrustTimeout(int userId, int shouldOverride) {
        long when = SystemClock.elapsedRealtime() + 14400000;
        int userId2 = this.mCurrentUser;
        TrustTimeoutAlarmListener alarm = this.mTrustTimeoutAlarmListenerForUser.get(Integer.valueOf(userId2));
        if (alarm == null) {
            alarm = new TrustTimeoutAlarmListener(userId2);
            this.mTrustTimeoutAlarmListenerForUser.put(Integer.valueOf(userId2), alarm);
        } else if (shouldOverride != 0 || !alarm.isQueued()) {
            this.mAlarmManager.cancel(alarm);
        } else if (DEBUG) {
            Slog.d(TAG, "Found existing trust timeout alarm. Skipping.");
            return;
        } else {
            return;
        }
        if (DEBUG) {
            Slog.d(TAG, "\tSetting up trust timeout alarm");
        }
        alarm.setQueued(true);
        this.mAlarmManager.setExact(2, when, TRUST_TIMEOUT_ALARM_TAG, alarm, this.mHandler);
    }

    private static final class AgentInfo {
        TrustAgentWrapper agent;
        ComponentName component;
        Drawable icon;
        CharSequence label;
        SettingsAttrs settings;
        int userId;

        private AgentInfo() {
        }

        public boolean equals(Object other) {
            if (!(other instanceof AgentInfo)) {
                return false;
            }
            AgentInfo o = (AgentInfo) other;
            if (!this.component.equals(o.component) || this.userId != o.userId) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (this.component.hashCode() * 31) + this.userId;
        }
    }

    private void updateTrustAll() {
        for (UserInfo userInfo : this.mUserManager.getUsers(true)) {
            updateTrust(userInfo.id, 0);
        }
    }

    public void updateTrust(int userId, int flags) {
        updateTrust(userId, flags, false);
    }

    /* access modifiers changed from: private */
    public void updateTrust(int userId, int flags, boolean isFromUnlock) {
        boolean changed;
        boolean managed = aggregateIsTrustManaged(userId);
        dispatchOnTrustManagedChanged(managed, userId);
        if (this.mStrongAuthTracker.isTrustAllowedForUser(userId) && isTrustUsuallyManagedInternal(userId) != managed) {
            updateTrustUsuallyManaged(userId, managed);
        }
        boolean trusted = aggregateIsTrusted(userId);
        boolean showingKeyguard = true;
        try {
            showingKeyguard = WindowManagerGlobal.getWindowManagerService().isKeyguardLocked();
        } catch (RemoteException e) {
        }
        synchronized (this.mUserIsTrusted) {
            boolean z = true;
            if (this.mSettingsObserver.getTrustAgentsExtendUnlock()) {
                trusted = trusted && (!showingKeyguard || isFromUnlock || !(this.mUserIsTrusted.get(userId) != trusted)) && userId == this.mCurrentUser;
                if (DEBUG) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Extend unlock setting trusted as ");
                    sb.append(Boolean.toString(trusted));
                    sb.append(" && ");
                    sb.append(Boolean.toString(!showingKeyguard));
                    sb.append(" && ");
                    sb.append(Boolean.toString(userId == this.mCurrentUser));
                    Slog.d(TAG, sb.toString());
                }
            }
            if (this.mUserIsTrusted.get(userId) == trusted) {
                z = false;
            }
            changed = z;
            this.mUserIsTrusted.put(userId, trusted);
        }
        dispatchOnTrustChanged(trusted, userId, flags);
        if (changed) {
            refreshDeviceLockedForUser(userId);
            if (!trusted) {
                maybeLockScreen(userId);
            } else {
                scheduleTrustTimeout(userId, false);
            }
        }
    }

    private void updateTrustUsuallyManaged(int userId, boolean managed) {
        synchronized (this.mTrustUsuallyManagedForUser) {
            this.mTrustUsuallyManagedForUser.put(userId, managed);
        }
        this.mHandler.removeMessages(10);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(10), JobStatus.DEFAULT_TRIGGER_MAX_DELAY);
    }

    public long addEscrowToken(byte[] token, int userId) {
        return this.mLockPatternUtils.addEscrowToken(token, userId, new LockPatternUtils.EscrowTokenStateChangeCallback() {
            public final void onEscrowTokenActivated(long j, int i) {
                TrustManagerService.this.lambda$addEscrowToken$0$TrustManagerService(j, i);
            }
        });
    }

    public boolean removeEscrowToken(long handle, int userId) {
        return this.mLockPatternUtils.removeEscrowToken(handle, userId);
    }

    public boolean isEscrowTokenActive(long handle, int userId) {
        return this.mLockPatternUtils.isEscrowTokenActive(handle, userId);
    }

    public void unlockUserWithToken(long handle, byte[] token, int userId) {
        this.mLockPatternUtils.unlockUserWithToken(handle, token, userId);
    }

    /* access modifiers changed from: package-private */
    public void showKeyguardErrorMessage(CharSequence message) {
        dispatchOnTrustError(message);
    }

    /* access modifiers changed from: package-private */
    public void refreshAgentList(int userIdOrAll) {
        List<UserInfo> userInfos;
        Iterator<UserInfo> it;
        PackageManager pm;
        List<UserInfo> userInfos2;
        int userIdOrAll2 = userIdOrAll;
        if (DEBUG) {
            Slog.d(TAG, "refreshAgentList(" + userIdOrAll2 + ")");
        }
        if (this.mTrustAgentsCanRun) {
            if (userIdOrAll2 != -1 && userIdOrAll2 < 0) {
                Log.e(TAG, "refreshAgentList(userId=" + userIdOrAll2 + "): Invalid user handle, must be USER_ALL or a specific user.", new Throwable("here"));
                userIdOrAll2 = -1;
            }
            PackageManager pm2 = this.mContext.getPackageManager();
            boolean z = true;
            if (userIdOrAll2 == -1) {
                userInfos = this.mUserManager.getUsers(true);
            } else {
                userInfos = new ArrayList<>();
                userInfos.add(this.mUserManager.getUserInfo(userIdOrAll2));
            }
            LockPatternUtils lockPatternUtils = this.mLockPatternUtils;
            ArraySet<AgentInfo> obsoleteAgents = new ArraySet<>();
            obsoleteAgents.addAll(this.mActiveAgents);
            Iterator<UserInfo> it2 = userInfos.iterator();
            while (it2.hasNext()) {
                UserInfo userInfo = it2.next();
                if (userInfo == null || userInfo.partial || !userInfo.isEnabled()) {
                    List<UserInfo> list = userInfos;
                    LockPatternUtils lockPatternUtils2 = lockPatternUtils;
                    Iterator<UserInfo> it3 = it2;
                    z = true;
                } else if (!userInfo.guestToRemove) {
                    if (!userInfo.supportsSwitchToByUser()) {
                        if (DEBUG) {
                            Slog.d(TAG, "refreshAgentList: skipping user " + userInfo.id + ": switchToByUser=false");
                        }
                    } else if (!this.mActivityManager.isUserRunning(userInfo.id)) {
                        if (DEBUG) {
                            Slog.d(TAG, "refreshAgentList: skipping user " + userInfo.id + ": user not started");
                        }
                    } else if (lockPatternUtils.isSecure(userInfo.id)) {
                        DevicePolicyManager dpm = lockPatternUtils.getDevicePolicyManager();
                        boolean disableTrustAgents = (dpm.getKeyguardDisabledFeatures((ComponentName) null, userInfo.id) & 16) != 0 ? z : false;
                        List<ComponentName> enabledAgents = lockPatternUtils.getEnabledTrustAgents(userInfo.id);
                        if (enabledAgents != null) {
                            List<ResolveInfo> resolveInfos = resolveAllowedTrustAgents(pm2, userInfo.id);
                            for (ResolveInfo resolveInfo : resolveInfos) {
                                ComponentName name = getComponentName(resolveInfo);
                                List<ResolveInfo> resolveInfos2 = resolveInfos;
                                List<UserInfo> userInfos3 = userInfos;
                                if (!enabledAgents.contains(name)) {
                                    if (DEBUG) {
                                        LockPatternUtils lockPatternUtils3 = lockPatternUtils;
                                        Slog.d(TAG, "refreshAgentList: skipping " + name.flattenToShortString() + " u" + userInfo.id + ": not enabled by user");
                                        lockPatternUtils = lockPatternUtils3;
                                    }
                                    resolveInfos = resolveInfos2;
                                    userInfos2 = userInfos3;
                                } else {
                                    LockPatternUtils lockPatternUtils4 = lockPatternUtils;
                                    if (disableTrustAgents) {
                                        it = it2;
                                        List<PersistableBundle> config = dpm.getTrustAgentConfiguration((ComponentName) null, name, userInfo.id);
                                        if (config == null || config.isEmpty()) {
                                            if (DEBUG) {
                                                Slog.d(TAG, "refreshAgentList: skipping " + name.flattenToShortString() + " u" + userInfo.id + ": not allowed by DPM");
                                            }
                                            lockPatternUtils = lockPatternUtils4;
                                            resolveInfos = resolveInfos2;
                                            userInfos2 = userInfos3;
                                            it2 = it;
                                        }
                                    } else {
                                        it = it2;
                                    }
                                    AgentInfo agentInfo = new AgentInfo();
                                    agentInfo.component = name;
                                    agentInfo.userId = userInfo.id;
                                    if (!this.mActiveAgents.contains(agentInfo)) {
                                        agentInfo.label = resolveInfo.loadLabel(pm2);
                                        agentInfo.icon = resolveInfo.loadIcon(pm2);
                                        agentInfo.settings = getSettingsAttrs(pm2, resolveInfo);
                                    } else {
                                        agentInfo = this.mActiveAgents.valueAt(this.mActiveAgents.indexOf(agentInfo));
                                    }
                                    boolean directUnlock = false;
                                    if (agentInfo.settings != null) {
                                        directUnlock = resolveInfo.serviceInfo.directBootAware && agentInfo.settings.canUnlockProfile;
                                    }
                                    if (directUnlock && DEBUG) {
                                        Slog.d(TAG, "refreshAgentList: trustagent " + name + "of user " + userInfo.id + "can unlock user profile.");
                                    }
                                    if (this.mUserManager.isUserUnlockingOrUnlocked(userInfo.id) || directUnlock) {
                                        if (!this.mStrongAuthTracker.canAgentsRunForUser(userInfo.id)) {
                                            int flag = this.mStrongAuthTracker.getStrongAuthForUser(userInfo.id);
                                            if (flag == 8) {
                                                pm = pm2;
                                                ResolveInfo resolveInfo2 = resolveInfo;
                                            } else if (flag == 1 && directUnlock) {
                                                pm = pm2;
                                                ResolveInfo resolveInfo3 = resolveInfo;
                                            } else if (DEBUG) {
                                                StringBuilder sb = new StringBuilder();
                                                sb.append("refreshAgentList: skipping user ");
                                                sb.append(userInfo.id);
                                                sb.append(": prevented by StrongAuthTracker = 0x");
                                                ResolveInfo resolveInfo4 = resolveInfo;
                                                sb.append(Integer.toHexString(this.mStrongAuthTracker.getStrongAuthForUser(userInfo.id)));
                                                Slog.d(TAG, sb.toString());
                                                lockPatternUtils = lockPatternUtils4;
                                                resolveInfos = resolveInfos2;
                                                userInfos2 = userInfos3;
                                                it2 = it;
                                                pm2 = pm2;
                                            } else {
                                                ResolveInfo resolveInfo5 = resolveInfo;
                                                lockPatternUtils = lockPatternUtils4;
                                                resolveInfos = resolveInfos2;
                                                userInfos2 = userInfos3;
                                                it2 = it;
                                            }
                                        } else {
                                            pm = pm2;
                                            ResolveInfo resolveInfo6 = resolveInfo;
                                        }
                                        if (agentInfo.agent == null) {
                                            agentInfo.agent = new TrustAgentWrapper(this.mContext, this, new Intent().setComponent(name), userInfo.getUserHandle());
                                        }
                                        if (!this.mActiveAgents.contains(agentInfo)) {
                                            this.mActiveAgents.add(agentInfo);
                                        } else {
                                            obsoleteAgents.remove(agentInfo);
                                        }
                                        lockPatternUtils = lockPatternUtils4;
                                        resolveInfos = resolveInfos2;
                                        userInfos2 = userInfos3;
                                        it2 = it;
                                        pm2 = pm;
                                    } else {
                                        if (DEBUG) {
                                            Slog.d(TAG, "refreshAgentList: skipping user " + userInfo.id + "'s trust agent " + name + ": FBE still locked and  the agent cannot unlock user profile.");
                                        }
                                        lockPatternUtils = lockPatternUtils4;
                                        resolveInfos = resolveInfos2;
                                        userInfos2 = userInfos3;
                                        it2 = it;
                                    }
                                }
                            }
                            PackageManager packageManager = pm2;
                            List<UserInfo> list2 = userInfos;
                            LockPatternUtils lockPatternUtils5 = lockPatternUtils;
                            Iterator<UserInfo> it4 = it2;
                            z = true;
                        } else if (DEBUG) {
                            Slog.d(TAG, "refreshAgentList: skipping user " + userInfo.id + ": no agents enabled by user");
                        }
                    } else if (DEBUG) {
                        Slog.d(TAG, "refreshAgentList: skipping user " + userInfo.id + ": no secure credential");
                    }
                }
            }
            List<UserInfo> list3 = userInfos;
            LockPatternUtils lockPatternUtils6 = lockPatternUtils;
            boolean trustMayHaveChanged = false;
            for (int i = 0; i < obsoleteAgents.size(); i++) {
                AgentInfo info = obsoleteAgents.valueAt(i);
                if (userIdOrAll2 == -1 || userIdOrAll2 == info.userId) {
                    if (info.agent.isManagingTrust()) {
                        trustMayHaveChanged = true;
                    }
                    info.agent.destroy();
                    this.mActiveAgents.remove(info);
                }
            }
            if (!trustMayHaveChanged) {
                return;
            }
            if (userIdOrAll2 == -1) {
                updateTrustAll();
            } else {
                updateTrust(userIdOrAll2, 0);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceLockedInner(int userId) {
        boolean z;
        synchronized (this.mDeviceLockedForUser) {
            z = this.mDeviceLockedForUser.get(userId, true);
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void refreshDeviceLockedForUser(int userId) {
        List<UserInfo> userInfos;
        if (userId != -1 && userId < 0) {
            Log.e(TAG, "refreshDeviceLockedForUser(userId=" + userId + "): Invalid user handle, must be USER_ALL or a specific user.", new Throwable("here"));
            userId = -1;
        }
        if (userId == -1) {
            userInfos = this.mUserManager.getUsers(true);
        } else {
            userInfos = new ArrayList<>();
            userInfos.add(this.mUserManager.getUserInfo(userId));
        }
        IWindowManager wm = WindowManagerGlobal.getWindowManagerService();
        for (int i = 0; i < userInfos.size(); i++) {
            UserInfo info = userInfos.get(i);
            if (info != null && !info.partial && info.isEnabled() && !info.guestToRemove) {
                int id = info.id;
                boolean secure = this.mLockPatternUtils.isSecure(id);
                boolean deviceLocked = false;
                if (info.supportsSwitchToByUser()) {
                    boolean trusted = aggregateIsTrusted(id);
                    boolean showingKeyguard = true;
                    boolean biometricAuthenticated = false;
                    if (this.mCurrentUser == id) {
                        synchronized (this.mUsersUnlockedByBiometric) {
                            biometricAuthenticated = this.mUsersUnlockedByBiometric.get(id, false);
                        }
                        try {
                            showingKeyguard = wm.isKeyguardLocked();
                        } catch (RemoteException e) {
                        }
                    }
                    if (secure && showingKeyguard && !trusted && !biometricAuthenticated) {
                        deviceLocked = true;
                    }
                    setDeviceLockedForUser(id, deviceLocked);
                } else if (info.isManagedProfile() && !secure) {
                    setDeviceLockedForUser(id, false);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void setDeviceLockedForUser(int userId, boolean locked) {
        boolean changed;
        synchronized (this.mDeviceLockedForUser) {
            changed = isDeviceLockedInner(userId) != locked;
            this.mDeviceLockedForUser.put(userId, locked);
        }
        if (changed) {
            dispatchDeviceLocked(userId, locked);
        }
    }

    private void dispatchDeviceLocked(int userId, boolean isLocked) {
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo agent = this.mActiveAgents.valueAt(i);
            if (agent.userId == userId) {
                if (isLocked) {
                    agent.agent.onDeviceLocked();
                } else {
                    agent.agent.onDeviceUnlocked();
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: dispatchEscrowTokenActivatedLocked */
    public void lambda$addEscrowToken$0$TrustManagerService(long handle, int userId) {
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo agent = this.mActiveAgents.valueAt(i);
            if (agent.userId == userId) {
                agent.agent.onEscrowTokenActivated(handle, userId);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateDevicePolicyFeatures() {
        boolean changed = false;
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo info = this.mActiveAgents.valueAt(i);
            if (info.agent.isConnected()) {
                info.agent.updateDevicePolicyFeatures();
                changed = true;
            }
        }
        if (changed) {
            this.mArchive.logDevicePolicyChanged();
        }
    }

    /* access modifiers changed from: private */
    public void removeAgentsOfPackage(String packageName) {
        boolean trustMayHaveChanged = false;
        for (int i = this.mActiveAgents.size() - 1; i >= 0; i--) {
            AgentInfo info = this.mActiveAgents.valueAt(i);
            if (packageName.equals(info.component.getPackageName())) {
                Log.i(TAG, "Resetting agent " + info.component.flattenToShortString());
                if (info.agent.isManagingTrust()) {
                    trustMayHaveChanged = true;
                }
                info.agent.destroy();
                this.mActiveAgents.removeAt(i);
            }
        }
        if (trustMayHaveChanged) {
            updateTrustAll();
        }
    }

    public void resetAgent(ComponentName name, int userId) {
        boolean trustMayHaveChanged = false;
        for (int i = this.mActiveAgents.size() - 1; i >= 0; i--) {
            AgentInfo info = this.mActiveAgents.valueAt(i);
            if (name.equals(info.component) && userId == info.userId) {
                Log.i(TAG, "Resetting agent " + info.component.flattenToShortString());
                if (info.agent.isManagingTrust()) {
                    trustMayHaveChanged = true;
                }
                info.agent.destroy();
                this.mActiveAgents.removeAt(i);
            }
        }
        if (trustMayHaveChanged) {
            updateTrust(userId, 0);
        }
        refreshAgentList(userId);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:34:0x007e, code lost:
        if (r4 == null) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0083, code lost:
        if (r4 == null) goto L_0x008b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0088, code lost:
        if (r4 == null) goto L_0x008b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private com.android.server.trust.TrustManagerService.SettingsAttrs getSettingsAttrs(android.content.pm.PackageManager r14, android.content.pm.ResolveInfo r15) {
        /*
            r13 = this;
            java.lang.String r0 = "TrustManagerService"
            r1 = 0
            if (r15 == 0) goto L_0x00d3
            android.content.pm.ServiceInfo r2 = r15.serviceInfo
            if (r2 == 0) goto L_0x00d3
            android.content.pm.ServiceInfo r2 = r15.serviceInfo
            android.os.Bundle r2 = r2.metaData
            if (r2 != 0) goto L_0x0011
            goto L_0x00d3
        L_0x0011:
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            android.content.pm.ServiceInfo r6 = r15.serviceInfo     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            java.lang.String r7 = "android.service.trust.trustagent"
            android.content.res.XmlResourceParser r6 = r6.loadXmlMetaData(r14, r7)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            r4 = r6
            if (r4 != 0) goto L_0x002c
            java.lang.String r6 = "Can't find android.service.trust.trustagent meta-data"
            android.util.Slog.w(r0, r6)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            if (r4 == 0) goto L_0x002b
            r4.close()
        L_0x002b:
            return r1
        L_0x002c:
            android.content.pm.ServiceInfo r6 = r15.serviceInfo     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            android.content.res.Resources r6 = r14.getResourcesForApplication(r6)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            android.util.AttributeSet r7 = android.util.Xml.asAttributeSet(r4)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
        L_0x0038:
            int r8 = r4.next()     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            r9 = r8
            r10 = 1
            r11 = 2
            if (r8 == r10) goto L_0x0044
            if (r9 == r11) goto L_0x0044
            goto L_0x0038
        L_0x0044:
            java.lang.String r8 = r4.getName()     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            java.lang.String r10 = "trust-agent"
            boolean r10 = r10.equals(r8)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            if (r10 != 0) goto L_0x005b
            java.lang.String r10 = "Meta-data does not start with trust-agent tag"
            android.util.Slog.w(r0, r10)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            r4.close()
            return r1
        L_0x005b:
            int[] r10 = com.android.internal.R.styleable.TrustAgent     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            android.content.res.TypedArray r10 = r6.obtainAttributes(r7, r10)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            java.lang.String r11 = r10.getString(r11)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            r2 = r11
            r11 = 3
            r12 = 0
            boolean r11 = r10.getBoolean(r11, r12)     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
            r3 = r11
            r10.recycle()     // Catch:{ NameNotFoundException -> 0x0086, IOException -> 0x0081, XmlPullParserException -> 0x007c, all -> 0x0075 }
        L_0x0071:
            r4.close()
            goto L_0x008b
        L_0x0075:
            r0 = move-exception
            if (r4 == 0) goto L_0x007b
            r4.close()
        L_0x007b:
            throw r0
        L_0x007c:
            r6 = move-exception
            r5 = r6
            if (r4 == 0) goto L_0x008b
            goto L_0x0071
        L_0x0081:
            r6 = move-exception
            r5 = r6
            if (r4 == 0) goto L_0x008b
            goto L_0x0071
        L_0x0086:
            r6 = move-exception
            r5 = r6
            if (r4 == 0) goto L_0x008b
            goto L_0x0071
        L_0x008b:
            if (r5 == 0) goto L_0x00a6
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Error parsing : "
            r6.append(r7)
            android.content.pm.ServiceInfo r7 = r15.serviceInfo
            java.lang.String r7 = r7.packageName
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Slog.w(r0, r6, r5)
            return r1
        L_0x00a6:
            if (r2 != 0) goto L_0x00a9
            return r1
        L_0x00a9:
            r0 = 47
            int r0 = r2.indexOf(r0)
            if (r0 >= 0) goto L_0x00c9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            android.content.pm.ServiceInfo r1 = r15.serviceInfo
            java.lang.String r1 = r1.packageName
            r0.append(r1)
            java.lang.String r1 = "/"
            r0.append(r1)
            r0.append(r2)
            java.lang.String r2 = r0.toString()
        L_0x00c9:
            com.android.server.trust.TrustManagerService$SettingsAttrs r0 = new com.android.server.trust.TrustManagerService$SettingsAttrs
            android.content.ComponentName r1 = android.content.ComponentName.unflattenFromString(r2)
            r0.<init>(r1, r3)
            return r0
        L_0x00d3:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.trust.TrustManagerService.getSettingsAttrs(android.content.pm.PackageManager, android.content.pm.ResolveInfo):com.android.server.trust.TrustManagerService$SettingsAttrs");
    }

    private ComponentName getComponentName(ResolveInfo resolveInfo) {
        if (resolveInfo == null || resolveInfo.serviceInfo == null) {
            return null;
        }
        return new ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name);
    }

    /* access modifiers changed from: private */
    public void maybeEnableFactoryTrustAgents(LockPatternUtils utils, int userId) {
        int i = userId;
        boolean shouldUseDefaultAgent = false;
        if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "trust_agents_initialized", 0, i) == 0) {
            List<ResolveInfo> resolveInfos = resolveAllowedTrustAgents(this.mContext.getPackageManager(), i);
            ComponentName defaultAgent = getDefaultFactoryTrustAgent(this.mContext);
            if (defaultAgent != null) {
                shouldUseDefaultAgent = true;
            }
            ArraySet<ComponentName> discoveredAgents = new ArraySet<>();
            if (shouldUseDefaultAgent) {
                discoveredAgents.add(defaultAgent);
                Log.i(TAG, "Enabling " + defaultAgent + " because it is a default agent.");
            } else {
                for (ResolveInfo resolveInfo : resolveInfos) {
                    ComponentName componentName = getComponentName(resolveInfo);
                    if ((resolveInfo.serviceInfo.applicationInfo.flags & 1) == 0) {
                        Log.i(TAG, "Leaving agent " + componentName + " disabled because package is not a system package.");
                    } else {
                        discoveredAgents.add(componentName);
                    }
                }
            }
            List<ComponentName> previouslyEnabledAgents = utils.getEnabledTrustAgents(userId);
            if (previouslyEnabledAgents != null) {
                discoveredAgents.addAll(previouslyEnabledAgents);
            }
            utils.setEnabledTrustAgents(discoveredAgents, i);
            Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "trust_agents_initialized", 1, i);
        }
    }

    private static ComponentName getDefaultFactoryTrustAgent(Context context) {
        String defaultTrustAgent = context.getResources().getString(17039737);
        if (TextUtils.isEmpty(defaultTrustAgent)) {
            return null;
        }
        return ComponentName.unflattenFromString(defaultTrustAgent);
    }

    private List<ResolveInfo> resolveAllowedTrustAgents(PackageManager pm, int userId) {
        List<ResolveInfo> resolveInfos = pm.queryIntentServicesAsUser(TRUST_AGENT_INTENT, 786560, userId);
        ArrayList<ResolveInfo> allowedAgents = new ArrayList<>(resolveInfos.size());
        for (ResolveInfo resolveInfo : resolveInfos) {
            if (!(resolveInfo.serviceInfo == null || resolveInfo.serviceInfo.applicationInfo == null)) {
                if (pm.checkPermission(PERMISSION_PROVIDE_AGENT, resolveInfo.serviceInfo.packageName) != 0) {
                    ComponentName name = getComponentName(resolveInfo);
                    Log.w(TAG, "Skipping agent " + name + " because package does not have permission " + PERMISSION_PROVIDE_AGENT + ".");
                } else {
                    allowedAgents.add(resolveInfo);
                }
            }
        }
        return allowedAgents;
    }

    /* access modifiers changed from: private */
    public boolean aggregateIsTrusted(int userId) {
        if (!this.mStrongAuthTracker.isTrustAllowedForUser(userId)) {
            return false;
        }
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo info = this.mActiveAgents.valueAt(i);
            if (info.userId == userId && info.agent.isTrusted()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean aggregateIsTrustManaged(int userId) {
        if (!this.mStrongAuthTracker.isTrustAllowedForUser(userId)) {
            return false;
        }
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo info = this.mActiveAgents.valueAt(i);
            if (info.userId == userId && info.agent.isManagingTrust()) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void dispatchUnlockAttempt(boolean successful, int userId) {
        if (successful) {
            this.mStrongAuthTracker.allowTrustFromUnlock(userId);
            updateTrust(userId, 0, true);
        }
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo info = this.mActiveAgents.valueAt(i);
            if (info.userId == userId) {
                info.agent.onUnlockAttempt(successful);
            }
        }
    }

    /* access modifiers changed from: private */
    public void dispatchUnlockLockout(int timeoutMs, int userId) {
        for (int i = 0; i < this.mActiveAgents.size(); i++) {
            AgentInfo info = this.mActiveAgents.valueAt(i);
            if (info.userId == userId) {
                info.agent.onUnlockLockout(timeoutMs);
            }
        }
    }

    /* access modifiers changed from: private */
    public void addListener(ITrustListener listener) {
        int i = 0;
        while (i < this.mTrustListeners.size()) {
            if (this.mTrustListeners.get(i).asBinder() != listener.asBinder()) {
                i++;
            } else {
                return;
            }
        }
        this.mTrustListeners.add(listener);
        updateTrustAll();
    }

    /* access modifiers changed from: private */
    public void removeListener(ITrustListener listener) {
        for (int i = 0; i < this.mTrustListeners.size(); i++) {
            if (this.mTrustListeners.get(i).asBinder() == listener.asBinder()) {
                this.mTrustListeners.remove(i);
                return;
            }
        }
    }

    private void dispatchOnTrustChanged(boolean enabled, int userId, int flags) {
        if (DEBUG) {
            Log.i(TAG, "onTrustChanged(" + enabled + ", " + userId + ", 0x" + Integer.toHexString(flags) + ")");
        }
        if (!enabled) {
            flags = 0;
        }
        int i = 0;
        while (i < this.mTrustListeners.size()) {
            try {
                this.mTrustListeners.get(i).onTrustChanged(enabled, userId, flags);
            } catch (DeadObjectException e) {
                Slog.d(TAG, "Removing dead TrustListener.");
                this.mTrustListeners.remove(i);
                i--;
            } catch (RemoteException e2) {
                Slog.e(TAG, "Exception while notifying TrustListener.", e2);
            }
            i++;
        }
    }

    private void dispatchOnTrustManagedChanged(boolean managed, int userId) {
        if (DEBUG) {
            Log.i(TAG, "onTrustManagedChanged(" + managed + ", " + userId + ")");
        }
        int i = 0;
        while (i < this.mTrustListeners.size()) {
            try {
                this.mTrustListeners.get(i).onTrustManagedChanged(managed, userId);
            } catch (DeadObjectException e) {
                Slog.d(TAG, "Removing dead TrustListener.");
                this.mTrustListeners.remove(i);
                i--;
            } catch (RemoteException e2) {
                Slog.e(TAG, "Exception while notifying TrustListener.", e2);
            }
            i++;
        }
    }

    private void dispatchOnTrustError(CharSequence message) {
        if (DEBUG) {
            Log.i(TAG, "onTrustError(" + message + ")");
        }
        int i = 0;
        while (i < this.mTrustListeners.size()) {
            try {
                this.mTrustListeners.get(i).onTrustError(message);
            } catch (DeadObjectException e) {
                Slog.d(TAG, "Removing dead TrustListener.");
                this.mTrustListeners.remove(i);
                i--;
            } catch (RemoteException e2) {
                Slog.e(TAG, "Exception while notifying TrustListener.", e2);
            }
            i++;
        }
    }

    public void onStartUser(int userId) {
        this.mHandler.obtainMessage(7, userId, 0, (Object) null).sendToTarget();
    }

    public void onCleanupUser(int userId) {
        this.mHandler.obtainMessage(8, userId, 0, (Object) null).sendToTarget();
    }

    public void onSwitchUser(int userId) {
        this.mHandler.obtainMessage(9, userId, 0, (Object) null).sendToTarget();
    }

    public void onUnlockUser(int userId) {
        this.mHandler.obtainMessage(11, userId, 0, (Object) null).sendToTarget();
    }

    public void onStopUser(int userId) {
        this.mHandler.obtainMessage(12, userId, 0, (Object) null).sendToTarget();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
        monitor-enter(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r0 = r4.mTrustUsuallyManagedForUser.indexOfKey(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0023, code lost:
        if (r0 < 0) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0025, code lost:
        r3 = r4.mTrustUsuallyManagedForUser.valueAt(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002b, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002c, code lost:
        return r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x002d, code lost:
        r4.mTrustUsuallyManagedForUser.put(r5, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0032, code lost:
        monitor-exit(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0033, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
        r1 = r4.mLockPatternUtils.isTrustUsuallyManaged(r5);
        r2 = r4.mTrustUsuallyManagedForUser;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isTrustUsuallyManagedInternal(int r5) {
        /*
            r4 = this;
            android.util.SparseBooleanArray r0 = r4.mTrustUsuallyManagedForUser
            monitor-enter(r0)
            android.util.SparseBooleanArray r1 = r4.mTrustUsuallyManagedForUser     // Catch:{ all -> 0x0037 }
            int r1 = r1.indexOfKey(r5)     // Catch:{ all -> 0x0037 }
            if (r1 < 0) goto L_0x0013
            android.util.SparseBooleanArray r2 = r4.mTrustUsuallyManagedForUser     // Catch:{ all -> 0x0037 }
            boolean r2 = r2.valueAt(r1)     // Catch:{ all -> 0x0037 }
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            return r2
        L_0x0013:
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            com.android.internal.widget.LockPatternUtils r0 = r4.mLockPatternUtils
            boolean r1 = r0.isTrustUsuallyManaged(r5)
            android.util.SparseBooleanArray r2 = r4.mTrustUsuallyManagedForUser
            monitor-enter(r2)
            android.util.SparseBooleanArray r0 = r4.mTrustUsuallyManagedForUser     // Catch:{ all -> 0x0034 }
            int r0 = r0.indexOfKey(r5)     // Catch:{ all -> 0x0034 }
            if (r0 < 0) goto L_0x002d
            android.util.SparseBooleanArray r3 = r4.mTrustUsuallyManagedForUser     // Catch:{ all -> 0x0034 }
            boolean r3 = r3.valueAt(r0)     // Catch:{ all -> 0x0034 }
            monitor-exit(r2)     // Catch:{ all -> 0x0034 }
            return r3
        L_0x002d:
            android.util.SparseBooleanArray r3 = r4.mTrustUsuallyManagedForUser     // Catch:{ all -> 0x0034 }
            r3.put(r5, r1)     // Catch:{ all -> 0x0034 }
            monitor-exit(r2)     // Catch:{ all -> 0x0034 }
            return r1
        L_0x0034:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0034 }
            throw r0
        L_0x0037:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.trust.TrustManagerService.isTrustUsuallyManagedInternal(int):boolean");
    }

    /* access modifiers changed from: private */
    public int resolveProfileParent(int userId) {
        long identity = Binder.clearCallingIdentity();
        try {
            UserInfo parent = this.mUserManager.getProfileParent(userId);
            if (parent != null) {
                return parent.getUserHandle().getIdentifier();
            }
            Binder.restoreCallingIdentity(identity);
            return userId;
        } finally {
            Binder.restoreCallingIdentity(identity);
        }
    }

    private static class SettingsAttrs {
        public boolean canUnlockProfile;
        public ComponentName componentName;

        public SettingsAttrs(ComponentName componentName2, boolean canUnlockProfile2) {
            this.componentName = componentName2;
            this.canUnlockProfile = canUnlockProfile2;
        }
    }

    private class Receiver extends BroadcastReceiver {
        private Receiver() {
        }

        public void onReceive(Context context, Intent intent) {
            int userId;
            String action = intent.getAction();
            if ("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED".equals(action)) {
                TrustManagerService.this.refreshAgentList(getSendingUserId());
                TrustManagerService.this.updateDevicePolicyFeatures();
            } else if ("android.intent.action.USER_ADDED".equals(action)) {
                int userId2 = getUserId(intent);
                if (userId2 > 0) {
                    TrustManagerService trustManagerService = TrustManagerService.this;
                    trustManagerService.maybeEnableFactoryTrustAgents(trustManagerService.mLockPatternUtils, userId2);
                }
            } else if ("android.intent.action.USER_REMOVED".equals(action) && (userId = getUserId(intent)) > 0) {
                synchronized (TrustManagerService.this.mUserIsTrusted) {
                    TrustManagerService.this.mUserIsTrusted.delete(userId);
                }
                synchronized (TrustManagerService.this.mDeviceLockedForUser) {
                    TrustManagerService.this.mDeviceLockedForUser.delete(userId);
                }
                synchronized (TrustManagerService.this.mTrustUsuallyManagedForUser) {
                    TrustManagerService.this.mTrustUsuallyManagedForUser.delete(userId);
                }
                synchronized (TrustManagerService.this.mUsersUnlockedByBiometric) {
                    TrustManagerService.this.mUsersUnlockedByBiometric.delete(userId);
                }
                TrustManagerService.this.refreshAgentList(userId);
                TrustManagerService.this.refreshDeviceLockedForUser(userId);
            }
        }

        private int getUserId(Intent intent) {
            int userId = intent.getIntExtra("android.intent.extra.user_handle", -100);
            if (userId > 0) {
                return userId;
            }
            Slog.wtf(TrustManagerService.TAG, "EXTRA_USER_HANDLE missing or invalid, value=" + userId);
            return -100;
        }

        public void register(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED");
            filter.addAction("android.intent.action.USER_ADDED");
            filter.addAction("android.intent.action.USER_REMOVED");
            context.registerReceiverAsUser(this, UserHandle.ALL, filter, (String) null, (Handler) null);
        }
    }

    private class StrongAuthTracker extends LockPatternUtils.StrongAuthTracker {
        SparseBooleanArray mStartFromSuccessfulUnlock = new SparseBooleanArray();

        public StrongAuthTracker(Context context) {
            super(context);
        }

        public void onStrongAuthRequiredChanged(int userId) {
            TrustTimeoutAlarmListener alarm;
            this.mStartFromSuccessfulUnlock.delete(userId);
            if (TrustManagerService.DEBUG) {
                Log.i(TrustManagerService.TAG, "onStrongAuthRequiredChanged(" + userId + ") -> trustAllowed=" + isTrustAllowedForUser(userId) + " agentsCanRun=" + canAgentsRunForUser(userId));
            }
            if (!isTrustAllowedForUser(userId) && (alarm = (TrustTimeoutAlarmListener) TrustManagerService.this.mTrustTimeoutAlarmListenerForUser.get(Integer.valueOf(userId))) != null && alarm.isQueued()) {
                alarm.setQueued(false);
                TrustManagerService.this.mAlarmManager.cancel(alarm);
            }
            TrustManagerService.this.refreshAgentList(userId);
            TrustManagerService.this.updateTrust(userId, 0);
        }

        /* access modifiers changed from: package-private */
        public boolean canAgentsRunForUser(int userId) {
            return this.mStartFromSuccessfulUnlock.get(userId) || TrustManagerService.super.isTrustAllowedForUser(userId);
        }

        /* access modifiers changed from: package-private */
        public void allowTrustFromUnlock(int userId) {
            if (userId >= 0) {
                boolean previous = canAgentsRunForUser(userId);
                this.mStartFromSuccessfulUnlock.put(userId, true);
                if (TrustManagerService.DEBUG) {
                    Log.i(TrustManagerService.TAG, "allowTrustFromUnlock(" + userId + ") -> trustAllowed=" + isTrustAllowedForUser(userId) + " agentsCanRun=" + canAgentsRunForUser(userId));
                }
                if (canAgentsRunForUser(userId) != previous) {
                    TrustManagerService.this.refreshAgentList(userId);
                    return;
                }
                return;
            }
            throw new IllegalArgumentException("userId must be a valid user: " + userId);
        }
    }

    private class TrustTimeoutAlarmListener implements AlarmManager.OnAlarmListener {
        private boolean mIsQueued = false;
        private final int mUserId;

        TrustTimeoutAlarmListener(int userId) {
            this.mUserId = userId;
        }

        public void onAlarm() {
            this.mIsQueued = false;
            int strongAuthForUser = TrustManagerService.this.mStrongAuthTracker.getStrongAuthForUser(this.mUserId);
            if (TrustManagerService.this.mStrongAuthTracker.isTrustAllowedForUser(this.mUserId)) {
                if (TrustManagerService.DEBUG) {
                    Slog.d(TrustManagerService.TAG, "Revoking all trust because of trust timeout");
                }
                LockPatternUtils access$400 = TrustManagerService.this.mLockPatternUtils;
                StrongAuthTracker unused = TrustManagerService.this.mStrongAuthTracker;
                access$400.requireStrongAuth(4, this.mUserId);
            }
            TrustManagerService.this.maybeLockScreen(this.mUserId);
        }

        public void setQueued(boolean isQueued) {
            this.mIsQueued = isQueued;
        }

        public boolean isQueued() {
            return this.mIsQueued;
        }
    }
}
