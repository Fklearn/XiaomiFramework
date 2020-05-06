package com.android.server.attention;

import android.app.ActivityManager;
import android.attention.AttentionManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.service.attention.IAttentionCallback;
import android.service.attention.IAttentionService;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.StatsLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.SystemService;
import com.android.server.attention.AttentionManagerService;
import com.android.server.pm.DumpState;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class AttentionManagerService extends SystemService {
    private static final long CONNECTION_TTL_MILLIS = 60000;
    private static final boolean DEBUG = false;
    private static final boolean DEFAULT_SERVICE_ENABLED = true;
    private static final String LOG_TAG = "AttentionManagerService";
    private static final String SERVICE_ENABLED = "service_enabled";
    private static final long STALE_AFTER_MILLIS = 5000;
    /* access modifiers changed from: private */
    public static String sTestAttentionServicePackage;
    private AttentionHandler mAttentionHandler;
    @VisibleForTesting
    ComponentName mComponentName;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final Object mLock;
    private final PowerManager mPowerManager;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public final SparseArray<UserState> mUserStates;

    public AttentionManagerService(Context context) {
        this(context, (PowerManager) context.getSystemService("power"), new Object(), (AttentionHandler) null);
        this.mAttentionHandler = new AttentionHandler();
    }

    @VisibleForTesting
    AttentionManagerService(Context context, PowerManager powerManager, Object lock, AttentionHandler handler) {
        super(context);
        this.mUserStates = new SparseArray<>();
        this.mContext = (Context) Preconditions.checkNotNull(context);
        this.mPowerManager = powerManager;
        this.mLock = lock;
        this.mAttentionHandler = handler;
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            this.mContext.registerReceiver(new ScreenStateReceiver(), new IntentFilter("android.intent.action.SCREEN_OFF"));
        }
    }

    public void onStart() {
        publishBinderService("attention", new BinderService());
        publishLocalService(AttentionManagerInternal.class, new LocalService());
    }

    public void onSwitchUser(int userId) {
        cancelAndUnbindLocked(peekUserStateLocked(userId));
    }

    public static boolean isServiceConfigured(Context context) {
        return !TextUtils.isEmpty(getServiceConfigPackage(context));
    }

    private boolean isServiceAvailable() {
        if (this.mComponentName == null) {
            this.mComponentName = resolveAttentionService(this.mContext);
        }
        return this.mComponentName != null;
    }

    /* access modifiers changed from: private */
    public boolean isAttentionServiceSupported() {
        return isServiceEnabled() && isServiceAvailable();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean isServiceEnabled() {
        return DeviceConfig.getBoolean("attention_manager_service", SERVICE_ENABLED, true);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005e, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0088, code lost:
        return true;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean checkAttention(long r12, android.attention.AttentionManagerInternal.AttentionCallbackInternal r14) {
        /*
            r11 = this;
            com.android.internal.util.Preconditions.checkNotNull(r14)
            boolean r0 = r11.isAttentionServiceSupported()
            r1 = 0
            if (r0 != 0) goto L_0x0012
            java.lang.String r0 = "AttentionManagerService"
            java.lang.String r2 = "Trying to call checkAttention() on an unsupported device."
            android.util.Slog.w(r0, r2)
            return r1
        L_0x0012:
            android.os.PowerManager r0 = r11.mPowerManager
            boolean r0 = r0.isInteractive()
            if (r0 != 0) goto L_0x001b
            return r1
        L_0x001b:
            java.lang.Object r0 = r11.mLock
            monitor-enter(r0)
            long r2 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x0089 }
            r11.freeIfInactiveLocked()     // Catch:{ all -> 0x0089 }
            com.android.server.attention.AttentionManagerService$UserState r4 = r11.getOrCreateCurrentUserStateLocked()     // Catch:{ all -> 0x0089 }
            r4.bindLocked()     // Catch:{ all -> 0x0089 }
            com.android.server.attention.AttentionManagerService$AttentionCheckCache r5 = r4.mAttentionCheckCache     // Catch:{ all -> 0x0089 }
            r6 = 1
            if (r5 == 0) goto L_0x0049
            long r7 = r5.mLastComputed     // Catch:{ all -> 0x0089 }
            r9 = 5000(0x1388, double:2.4703E-320)
            long r7 = r7 + r9
            int r7 = (r2 > r7 ? 1 : (r2 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x0049
            int r1 = r5.mResult     // Catch:{ all -> 0x0089 }
            long r7 = r5.mTimestamp     // Catch:{ all -> 0x0089 }
            r14.onSuccess(r1, r7)     // Catch:{ all -> 0x0089 }
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            return r6
        L_0x0049:
            com.android.server.attention.AttentionManagerService$AttentionCheck r7 = r4.mCurrentAttentionCheck     // Catch:{ all -> 0x0089 }
            if (r7 == 0) goto L_0x005f
            com.android.server.attention.AttentionManagerService$AttentionCheck r7 = r4.mCurrentAttentionCheck     // Catch:{ all -> 0x0089 }
            boolean r7 = r7.mIsDispatched     // Catch:{ all -> 0x0089 }
            if (r7 == 0) goto L_0x005d
            com.android.server.attention.AttentionManagerService$AttentionCheck r7 = r4.mCurrentAttentionCheck     // Catch:{ all -> 0x0089 }
            boolean r7 = r7.mIsFulfilled     // Catch:{ all -> 0x0089 }
            if (r7 != 0) goto L_0x005f
        L_0x005d:
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            return r1
        L_0x005f:
            com.android.server.attention.AttentionManagerService$AttentionCheck r7 = r11.createAttentionCheck(r14, r4)     // Catch:{ all -> 0x0089 }
            r4.mCurrentAttentionCheck = r7     // Catch:{ all -> 0x0089 }
            android.service.attention.IAttentionService r7 = r4.mService     // Catch:{ all -> 0x0089 }
            if (r7 == 0) goto L_0x0087
            r11.cancelAfterTimeoutLocked(r12)     // Catch:{ RemoteException -> 0x007d }
            android.service.attention.IAttentionService r7 = r4.mService     // Catch:{ RemoteException -> 0x007d }
            com.android.server.attention.AttentionManagerService$AttentionCheck r8 = r4.mCurrentAttentionCheck     // Catch:{ RemoteException -> 0x007d }
            android.service.attention.IAttentionCallback r8 = r8.mIAttentionCallback     // Catch:{ RemoteException -> 0x007d }
            r7.checkAttention(r8)     // Catch:{ RemoteException -> 0x007d }
            com.android.server.attention.AttentionManagerService$AttentionCheck r7 = r4.mCurrentAttentionCheck     // Catch:{ RemoteException -> 0x007d }
            boolean unused = r7.mIsDispatched = r6     // Catch:{ RemoteException -> 0x007d }
            goto L_0x0087
        L_0x007d:
            r6 = move-exception
            java.lang.String r7 = "AttentionManagerService"
            java.lang.String r8 = "Cannot call into the AttentionService"
            android.util.Slog.e(r7, r8)     // Catch:{ all -> 0x0089 }
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            return r1
        L_0x0087:
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            return r6
        L_0x0089:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0089 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.attention.AttentionManagerService.checkAttention(long, android.attention.AttentionManagerInternal$AttentionCallbackInternal):boolean");
    }

    private AttentionCheck createAttentionCheck(final AttentionManagerInternal.AttentionCallbackInternal callbackInternal, final UserState userState) {
        return new AttentionCheck(callbackInternal, new IAttentionCallback.Stub() {
            public void onSuccess(int result, long timestamp) {
                if (!userState.mCurrentAttentionCheck.mIsFulfilled) {
                    callbackInternal.onSuccess(result, timestamp);
                    boolean unused = userState.mCurrentAttentionCheck.mIsFulfilled = true;
                }
                synchronized (AttentionManagerService.this.mLock) {
                    userState.mAttentionCheckCache = new AttentionCheckCache(SystemClock.uptimeMillis(), result, timestamp);
                }
                StatsLog.write(143, result);
            }

            public void onFailure(int error) {
                if (!userState.mCurrentAttentionCheck.mIsFulfilled) {
                    callbackInternal.onFailure(error);
                    boolean unused = userState.mCurrentAttentionCheck.mIsFulfilled = true;
                }
                StatsLog.write(143, error);
            }
        });
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void cancelAttentionCheck(AttentionManagerInternal.AttentionCallbackInternal callbackInternal) {
        synchronized (this.mLock) {
            UserState userState = peekCurrentUserStateLocked();
            if (userState != null) {
                if (!userState.mCurrentAttentionCheck.mCallbackInternal.equals(callbackInternal)) {
                    Slog.w(LOG_TAG, "Cannot cancel a non-current request");
                } else {
                    cancel(userState);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mLock"})
    @VisibleForTesting
    public void freeIfInactiveLocked() {
        this.mAttentionHandler.removeMessages(1);
        this.mAttentionHandler.sendEmptyMessageDelayed(1, 60000);
    }

    @GuardedBy({"mLock"})
    private void cancelAfterTimeoutLocked(long timeout) {
        this.mAttentionHandler.sendEmptyMessageDelayed(2, timeout);
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mLock"})
    @VisibleForTesting
    public UserState getOrCreateCurrentUserStateLocked() {
        return getOrCreateUserStateLocked(ActivityManager.getCurrentUser());
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mLock"})
    @VisibleForTesting
    public UserState getOrCreateUserStateLocked(int userId) {
        UserState result = this.mUserStates.get(userId);
        if (result != null) {
            return result;
        }
        UserState result2 = new UserState(userId, this.mContext, this.mLock, this.mAttentionHandler, this.mComponentName);
        this.mUserStates.put(userId, result2);
        return result2;
    }

    /* access modifiers changed from: protected */
    @GuardedBy({"mLock"})
    @VisibleForTesting
    public UserState peekCurrentUserStateLocked() {
        return peekUserStateLocked(ActivityManager.getCurrentUser());
    }

    @GuardedBy({"mLock"})
    private UserState peekUserStateLocked(int userId) {
        return this.mUserStates.get(userId);
    }

    private static String getServiceConfigPackage(Context context) {
        return context.getPackageManager().getAttentionServicePackageName();
    }

    /* access modifiers changed from: private */
    public static ComponentName resolveAttentionService(Context context) {
        String resolvedPackage;
        String serviceConfigPackage = getServiceConfigPackage(context);
        int flags = DumpState.DUMP_DEXOPT;
        if (!TextUtils.isEmpty(sTestAttentionServicePackage)) {
            resolvedPackage = sTestAttentionServicePackage;
            flags = 128;
        } else if (TextUtils.isEmpty(serviceConfigPackage)) {
            return null;
        } else {
            resolvedPackage = serviceConfigPackage;
        }
        ResolveInfo resolveInfo = context.getPackageManager().resolveService(new Intent("android.service.attention.AttentionService").setPackage(resolvedPackage), flags);
        if (resolveInfo == null || resolveInfo.serviceInfo == null) {
            Slog.wtf(LOG_TAG, String.format("Service %s not found in package %s", new Object[]{"android.service.attention.AttentionService", serviceConfigPackage}));
            return null;
        }
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        if ("android.permission.BIND_ATTENTION_SERVICE".equals(serviceInfo.permission)) {
            return serviceInfo.getComponentName();
        }
        Slog.e(LOG_TAG, String.format("Service %s should require %s permission. Found %s permission", new Object[]{serviceInfo.getComponentName(), "android.permission.BIND_ATTENTION_SERVICE", serviceInfo.permission}));
        return null;
    }

    /* access modifiers changed from: private */
    public void dumpInternal(IndentingPrintWriter ipw) {
        ipw.println("Attention Manager Service (dumpsys attention) state:\n");
        ipw.println("AttentionServicePackageName=" + getServiceConfigPackage(this.mContext));
        ipw.println("Resolved component:");
        if (this.mComponentName != null) {
            ipw.increaseIndent();
            ipw.println("Component=" + this.mComponentName.getPackageName());
            ipw.println("Class=" + this.mComponentName.getClassName());
            ipw.decreaseIndent();
        }
        synchronized (this.mLock) {
            int size = this.mUserStates.size();
            ipw.print("Number user states: ");
            ipw.println(size);
            if (size > 0) {
                ipw.increaseIndent();
                for (int i = 0; i < size; i++) {
                    ipw.print(i);
                    ipw.print(":");
                    this.mUserStates.valueAt(i).dump(ipw);
                    ipw.println();
                }
                ipw.decreaseIndent();
            }
        }
    }

    private final class LocalService extends AttentionManagerInternal {
        private LocalService() {
        }

        public boolean isAttentionServiceSupported() {
            return AttentionManagerService.this.isAttentionServiceSupported();
        }

        public boolean checkAttention(long timeout, AttentionManagerInternal.AttentionCallbackInternal callbackInternal) {
            return AttentionManagerService.this.checkAttention(timeout, callbackInternal);
        }

        public void cancelAttentionCheck(AttentionManagerInternal.AttentionCallbackInternal callbackInternal) {
            AttentionManagerService.this.cancelAttentionCheck(callbackInternal);
        }
    }

    private static final class AttentionCheckCache {
        /* access modifiers changed from: private */
        public final long mLastComputed;
        /* access modifiers changed from: private */
        public final int mResult;
        /* access modifiers changed from: private */
        public final long mTimestamp;

        AttentionCheckCache(long lastComputed, int result, long timestamp) {
            this.mLastComputed = lastComputed;
            this.mResult = result;
            this.mTimestamp = timestamp;
        }
    }

    @VisibleForTesting
    static final class AttentionCheck {
        /* access modifiers changed from: private */
        public final AttentionManagerInternal.AttentionCallbackInternal mCallbackInternal;
        /* access modifiers changed from: private */
        public final IAttentionCallback mIAttentionCallback;
        /* access modifiers changed from: private */
        public boolean mIsDispatched;
        /* access modifiers changed from: private */
        public boolean mIsFulfilled;

        AttentionCheck(AttentionManagerInternal.AttentionCallbackInternal callbackInternal, IAttentionCallback iAttentionCallback) {
            this.mCallbackInternal = callbackInternal;
            this.mIAttentionCallback = iAttentionCallback;
        }

        /* access modifiers changed from: package-private */
        public void cancelInternal() {
            this.mIsFulfilled = true;
            this.mCallbackInternal.onFailure(3);
        }
    }

    @VisibleForTesting
    protected static class UserState {
        @GuardedBy({"mLock"})
        AttentionCheckCache mAttentionCheckCache;
        private final Handler mAttentionHandler;
        /* access modifiers changed from: private */
        @GuardedBy({"mLock"})
        public boolean mBinding;
        private final ComponentName mComponentName;
        /* access modifiers changed from: private */
        public final AttentionServiceConnection mConnection = new AttentionServiceConnection();
        private final Context mContext;
        @GuardedBy({"mLock"})
        AttentionCheck mCurrentAttentionCheck;
        /* access modifiers changed from: private */
        public final Object mLock;
        @GuardedBy({"mLock"})
        IAttentionService mService;
        /* access modifiers changed from: private */
        public final int mUserId;

        UserState(int userId, Context context, Object lock, Handler handler, ComponentName componentName) {
            this.mUserId = userId;
            this.mContext = (Context) Preconditions.checkNotNull(context);
            this.mLock = Preconditions.checkNotNull(lock);
            this.mComponentName = (ComponentName) Preconditions.checkNotNull(componentName);
            this.mAttentionHandler = handler;
        }

        /* access modifiers changed from: private */
        @GuardedBy({"mLock"})
        public void handlePendingCallbackLocked() {
            if (!this.mCurrentAttentionCheck.mIsDispatched) {
                IAttentionService iAttentionService = this.mService;
                if (iAttentionService != null) {
                    try {
                        iAttentionService.checkAttention(this.mCurrentAttentionCheck.mIAttentionCallback);
                        boolean unused = this.mCurrentAttentionCheck.mIsDispatched = true;
                    } catch (RemoteException e) {
                        Slog.e(AttentionManagerService.LOG_TAG, "Cannot call into the AttentionService");
                    }
                } else {
                    this.mCurrentAttentionCheck.mCallbackInternal.onFailure(2);
                }
            }
        }

        /* access modifiers changed from: private */
        @GuardedBy({"mLock"})
        public void bindLocked() {
            if (!this.mBinding && this.mService == null) {
                this.mBinding = true;
                this.mAttentionHandler.post(new Runnable() {
                    public final void run() {
                        AttentionManagerService.UserState.this.lambda$bindLocked$0$AttentionManagerService$UserState();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$bindLocked$0$AttentionManagerService$UserState() {
            this.mContext.bindServiceAsUser(new Intent("android.service.attention.AttentionService").setComponent(this.mComponentName), this.mConnection, 1, UserHandle.CURRENT);
        }

        /* access modifiers changed from: private */
        public void dump(IndentingPrintWriter pw) {
            pw.println("userId=" + this.mUserId);
            synchronized (this.mLock) {
                pw.println("binding=" + this.mBinding);
                pw.println("current attention check:");
                if (this.mCurrentAttentionCheck != null) {
                    pw.increaseIndent();
                    pw.println("is dispatched=" + this.mCurrentAttentionCheck.mIsDispatched);
                    pw.println("is fulfilled:=" + this.mCurrentAttentionCheck.mIsFulfilled);
                    pw.decreaseIndent();
                }
                pw.println("attention check cache:");
                if (this.mAttentionCheckCache != null) {
                    pw.increaseIndent();
                    pw.println("last computed=" + this.mAttentionCheckCache.mLastComputed);
                    pw.println("timestamp=" + this.mAttentionCheckCache.mTimestamp);
                    pw.println("result=" + this.mAttentionCheckCache.mResult);
                    pw.decreaseIndent();
                }
            }
        }

        private class AttentionServiceConnection implements ServiceConnection {
            private AttentionServiceConnection() {
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                init(IAttentionService.Stub.asInterface(service));
            }

            public void onServiceDisconnected(ComponentName name) {
                cleanupService();
            }

            public void onBindingDied(ComponentName name) {
                cleanupService();
            }

            public void onNullBinding(ComponentName name) {
                cleanupService();
            }

            /* access modifiers changed from: package-private */
            public void cleanupService() {
                init((IAttentionService) null);
            }

            private void init(IAttentionService service) {
                synchronized (UserState.this.mLock) {
                    UserState.this.mService = service;
                    boolean unused = UserState.this.mBinding = false;
                    UserState.this.handlePendingCallbackLocked();
                }
            }
        }
    }

    @VisibleForTesting
    protected class AttentionHandler extends Handler {
        private static final int ATTENTION_CHECK_TIMEOUT = 2;
        private static final int CHECK_CONNECTION_EXPIRATION = 1;

        AttentionHandler() {
            super(Looper.myLooper());
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                for (int i2 = 0; i2 < AttentionManagerService.this.mUserStates.size(); i2++) {
                    AttentionManagerService attentionManagerService = AttentionManagerService.this;
                    attentionManagerService.cancelAndUnbindLocked((UserState) attentionManagerService.mUserStates.valueAt(i2));
                }
            } else if (i == 2) {
                synchronized (AttentionManagerService.this.mLock) {
                    AttentionManagerService.this.cancel(AttentionManagerService.this.peekCurrentUserStateLocked());
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void cancel(UserState userState) {
        if (userState != null && userState.mCurrentAttentionCheck != null && !userState.mCurrentAttentionCheck.mIsFulfilled) {
            if (userState.mService == null) {
                userState.mCurrentAttentionCheck.cancelInternal();
                return;
            }
            try {
                userState.mService.cancelAttentionCheck(userState.mCurrentAttentionCheck.mIAttentionCallback);
            } catch (RemoteException e) {
                Slog.e(LOG_TAG, "Unable to cancel attention check");
                userState.mCurrentAttentionCheck.cancelInternal();
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void cancelAndUnbindLocked(UserState userState) {
        synchronized (this.mLock) {
            if (userState != null) {
                cancel(userState);
                if (userState.mService != null) {
                    this.mAttentionHandler.post(new Runnable(userState) {
                        private final /* synthetic */ AttentionManagerService.UserState f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void run() {
                            AttentionManagerService.this.lambda$cancelAndUnbindLocked$0$AttentionManagerService(this.f$1);
                        }
                    });
                    userState.mConnection.cleanupService();
                    this.mUserStates.remove(userState.mUserId);
                }
            }
        }
    }

    public /* synthetic */ void lambda$cancelAndUnbindLocked$0$AttentionManagerService(UserState userState) {
        this.mContext.unbindService(userState.mConnection);
    }

    private final class ScreenStateReceiver extends BroadcastReceiver {
        private ScreenStateReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                AttentionManagerService attentionManagerService = AttentionManagerService.this;
                attentionManagerService.cancelAndUnbindLocked(attentionManagerService.peekCurrentUserStateLocked());
            }
        }
    }

    private final class AttentionManagerServiceShellCommand extends ShellCommand {
        final TestableAttentionCallbackInternal mTestableAttentionCallback;

        class TestableAttentionCallbackInternal extends AttentionManagerInternal.AttentionCallbackInternal {
            private int mLastCallbackCode = -1;

            TestableAttentionCallbackInternal() {
            }

            public void onSuccess(int result, long timestamp) {
                this.mLastCallbackCode = result;
            }

            public void onFailure(int error) {
                this.mLastCallbackCode = error;
            }

            public void reset() {
                this.mLastCallbackCode = -1;
            }

            public int getLastCallbackCode() {
                return this.mLastCallbackCode;
            }
        }

        private AttentionManagerServiceShellCommand() {
            this.mTestableAttentionCallback = new TestableAttentionCallbackInternal();
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* JADX WARNING: Removed duplicated region for block: B:50:0x0098 A[Catch:{ IllegalArgumentException -> 0x00b1 }] */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x00a7 A[Catch:{ IllegalArgumentException -> 0x00b1 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public int onCommand(java.lang.String r9) {
            /*
                r8 = this;
                if (r9 != 0) goto L_0x0007
                int r0 = r8.handleDefaultCommands(r9)
                return r0
            L_0x0007:
                java.io.PrintWriter r0 = r8.getErrPrintWriter()
                r1 = -1
                int r2 = r9.hashCode()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                r3 = 0
                r4 = 4
                r5 = 3
                r6 = 2
                r7 = 1
                switch(r2) {
                    case -1208709968: goto L_0x0042;
                    case -1002424240: goto L_0x0038;
                    case -415045819: goto L_0x002d;
                    case 3045982: goto L_0x0023;
                    case 1193447472: goto L_0x0019;
                    default: goto L_0x0018;
                }     // Catch:{ IllegalArgumentException -> 0x00b1 }
            L_0x0018:
                goto L_0x004c
            L_0x0019:
                java.lang.String r2 = "clearTestableAttentionService"
                boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                if (r2 == 0) goto L_0x0018
                r2 = r5
                goto L_0x004d
            L_0x0023:
                java.lang.String r2 = "call"
                boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                if (r2 == 0) goto L_0x0018
                r2 = r7
                goto L_0x004d
            L_0x002d:
                java.lang.String r2 = "setTestableAttentionService"
                boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                if (r2 == 0) goto L_0x0018
                r2 = r6
                goto L_0x004d
            L_0x0038:
                java.lang.String r2 = "getAttentionServiceComponent"
                boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                if (r2 == 0) goto L_0x0018
                r2 = r3
                goto L_0x004d
            L_0x0042:
                java.lang.String r2 = "getLastTestCallbackCode"
                boolean r2 = r9.equals(r2)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                if (r2 == 0) goto L_0x0018
                r2 = r4
                goto L_0x004d
            L_0x004c:
                r2 = r1
            L_0x004d:
                if (r2 == 0) goto L_0x00ac
                if (r2 == r7) goto L_0x006f
                if (r2 == r6) goto L_0x0066
                if (r2 == r5) goto L_0x0061
                if (r2 == r4) goto L_0x005c
                int r1 = r8.handleDefaultCommands(r9)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                return r1
            L_0x005c:
                int r1 = r8.cmdGetLastTestCallbackCode()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                return r1
            L_0x0061:
                int r1 = r8.cmdClearTestableAttentionService()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                return r1
            L_0x0066:
                java.lang.String r2 = r8.getNextArgRequired()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                int r1 = r8.cmdSetTestableAttentionService(r2)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                return r1
            L_0x006f:
                java.lang.String r2 = r8.getNextArgRequired()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                int r4 = r2.hashCode()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                r5 = 763077136(0x2d7ba210, float:1.4303683E-11)
                if (r4 == r5) goto L_0x008b
                r5 = 1485997302(0x589284f6, float:1.28879808E15)
                if (r4 == r5) goto L_0x0082
            L_0x0081:
                goto L_0x0095
            L_0x0082:
                java.lang.String r4 = "checkAttention"
                boolean r2 = r2.equals(r4)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                if (r2 == 0) goto L_0x0081
                goto L_0x0096
            L_0x008b:
                java.lang.String r3 = "cancelCheckAttention"
                boolean r2 = r2.equals(r3)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                if (r2 == 0) goto L_0x0081
                r3 = r7
                goto L_0x0096
            L_0x0095:
                r3 = r1
            L_0x0096:
                if (r3 == 0) goto L_0x00a7
                if (r3 != r7) goto L_0x009f
                int r1 = r8.cmdCallCancelAttention()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                return r1
            L_0x009f:
                java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException     // Catch:{ IllegalArgumentException -> 0x00b1 }
                java.lang.String r3 = "Invalid argument"
                r2.<init>(r3)     // Catch:{ IllegalArgumentException -> 0x00b1 }
                throw r2     // Catch:{ IllegalArgumentException -> 0x00b1 }
            L_0x00a7:
                int r1 = r8.cmdCallCheckAttention()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                return r1
            L_0x00ac:
                int r1 = r8.cmdResolveAttentionServiceComponent()     // Catch:{ IllegalArgumentException -> 0x00b1 }
                return r1
            L_0x00b1:
                r2 = move-exception
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Error: "
                r3.append(r4)
                java.lang.String r4 = r2.getMessage()
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                r0.println(r3)
                return r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.attention.AttentionManagerService.AttentionManagerServiceShellCommand.onCommand(java.lang.String):int");
        }

        private int cmdSetTestableAttentionService(String testingServicePackage) {
            PrintWriter out = getOutPrintWriter();
            String str = "false";
            if (TextUtils.isEmpty(testingServicePackage)) {
                out.println(str);
                return 0;
            }
            String unused = AttentionManagerService.sTestAttentionServicePackage = testingServicePackage;
            resetStates();
            if (AttentionManagerService.this.mComponentName != null) {
                str = "true";
            }
            out.println(str);
            return 0;
        }

        private int cmdClearTestableAttentionService() {
            String unused = AttentionManagerService.sTestAttentionServicePackage = "";
            this.mTestableAttentionCallback.reset();
            resetStates();
            return 0;
        }

        private int cmdCallCheckAttention() {
            getOutPrintWriter().println(AttentionManagerService.this.checkAttention(2000, this.mTestableAttentionCallback) ? "true" : "false");
            return 0;
        }

        private int cmdCallCancelAttention() {
            PrintWriter out = getOutPrintWriter();
            AttentionManagerService.this.cancelAttentionCheck(this.mTestableAttentionCallback);
            out.println("true");
            return 0;
        }

        private int cmdResolveAttentionServiceComponent() {
            PrintWriter out = getOutPrintWriter();
            ComponentName resolvedComponent = AttentionManagerService.resolveAttentionService(AttentionManagerService.this.mContext);
            out.println(resolvedComponent != null ? resolvedComponent.flattenToShortString() : "");
            return 0;
        }

        private int cmdGetLastTestCallbackCode() {
            getOutPrintWriter().println(this.mTestableAttentionCallback.getLastCallbackCode());
            return 0;
        }

        private void resetStates() {
            AttentionManagerService attentionManagerService = AttentionManagerService.this;
            attentionManagerService.mComponentName = AttentionManagerService.resolveAttentionService(attentionManagerService.mContext);
            AttentionManagerService.this.mUserStates.clear();
        }

        public void onHelp() {
            PrintWriter out = getOutPrintWriter();
            out.println("Attention commands: ");
            out.println("  setTestableAttentionService <service_package>: Bind to a custom implementation of attention service");
            out.println("  ---<service_package>:");
            out.println("       := Package containing the Attention Service implementation to bind to");
            out.println("  ---returns:");
            out.println("       := true, if was bound successfully");
            out.println("       := false, if was not bound successfully");
            out.println("  clearTestableAttentionService: Undo custom bindings. Revert to previous behavior");
            out.println("  getAttentionServiceComponent: Get the current service component string");
            out.println("  ---returns:");
            out.println("       := If valid, the component string (in shorten form) for the currently bound service.");
            out.println("       := else, empty string");
            out.println("  call checkAttention: Calls check attention");
            out.println("  ---returns:");
            out.println("       := true, if the call was successfully dispatched to the service implementation. (to see the result, call getLastTestCallbackCode)");
            out.println("       := false, otherwise");
            out.println("  call cancelCheckAttention: Cancels check attention");
            out.println("  getLastTestCallbackCode");
            out.println("  ---returns:");
            out.println("       := An integer, representing the last callback code received from the bounded implementation. If none, it will return -1");
        }
    }

    private final class BinderService extends Binder {
        AttentionManagerServiceShellCommand mAttentionManagerServiceShellCommand;

        private BinderService() {
            this.mAttentionManagerServiceShellCommand = new AttentionManagerServiceShellCommand();
        }

        public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) {
            this.mAttentionManagerServiceShellCommand.exec(this, in, out, err, args, callback, resultReceiver);
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(AttentionManagerService.this.mContext, AttentionManagerService.LOG_TAG, pw)) {
                AttentionManagerService.this.dumpInternal(new IndentingPrintWriter(pw, "  "));
            }
        }
    }
}
