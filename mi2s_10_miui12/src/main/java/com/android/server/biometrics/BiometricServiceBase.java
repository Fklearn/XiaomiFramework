package com.android.server.biometrics;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.AppOpsManager;
import android.app.IActivityTaskManager;
import android.app.SynchronousUserSwitchObserver;
import android.app.TaskStackListener;
import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.UserInfo;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.IBiometricService;
import android.hardware.biometrics.IBiometricServiceLockoutResetCallback;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.hardware.fingerprint.Fingerprint;
import android.os.Binder;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.HwBinder;
import android.os.HwParcel;
import android.os.IBinder;
import android.os.IHwBinder;
import android.os.IRemoteCallback;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.security.KeyStore;
import android.util.Slog;
import android.util.StatsLog;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.statusbar.IStatusBarService;
import com.android.server.BatteryService;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.UiModeManagerService;
import com.android.server.biometrics.BiometricServiceBase;
import com.android.server.fingerprint.FingerprintServiceInjector;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.slice.SliceClientPermissions;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BiometricServiceBase extends SystemService implements IHwBinder.DeathRecipient {
    private static final long CANCEL_TIMEOUT_LIMIT = 3000;
    private static final boolean CLEANUP_UNKNOWN_TEMPLATES = true;
    private static final int CMD_APP_AUTHEN = 1;
    private static final int CMD_APP_CANCEL_AUTHEN = 2;
    private static final int CMD_APP_CANCEL_ENROLL = 8;
    private static final int CMD_APP_ENROLL = 7;
    private static final int CMD_FW_LOCK_CANCEL = 5;
    private static final int CMD_FW_TOP_APP_CANCEL = 6;
    private static final int CMD_VENDOR_AUTHENTICATED = 3;
    private static final int CMD_VENDOR_ENROLL_RES = 9;
    private static final int CMD_VENDOR_ERROR = 4;
    private static final int CODE_EXT_CMD = 1;
    private static final int CODE_PROCESS_CMD = 1;
    protected static final boolean DEBUG = true;
    private static final String DEFAULT_PACKNAME = "";
    private static final int DEFAULT_PARAM = 0;
    private static final String EXT_DESCRIPTOR = "vendor.xiaomi.hardware.fingerprintextension@1.0::IXiaomiFingerprint";
    private static final String FOD_SERVICE_NAME = "android.app.fod.ICallback";
    private static final String INTERFACE_DESCRIPTOR = "android.app.fod.ICallback";
    public static boolean IS_FOD = SystemProperties.getBoolean("ro.hardware.fp.fod", false);
    private static final String KEY_LOCKOUT_RESET_USER = "lockout_reset_user";
    private static final int MSG_USER_SWITCHING = 10;
    private static final String NAME_EXT_DAEMON = "vendor.xiaomi.hardware.fingerprintextension@1.0::IXiaomiFingerprint";
    private static final int TOUCH_AUTHEN = 1;
    private static final int TOUCH_CANCEL = 0;
    private static final int TOUCH_ENROLL = 2;
    private static final String TOUCH_FOD_STATUS = "/sys/class/touch/tp_dev/fod_status";
    private static final int TOUCH_FW_LOCK_CANCEL = 3;
    /* access modifiers changed from: private */
    public final IActivityTaskManager mActivityTaskManager;
    protected final AppOpsManager mAppOps;
    protected final Map<Integer, Long> mAuthenticatorIds = Collections.synchronizedMap(new HashMap());
    private IBiometricService mBiometricService;
    protected boolean mConfigNeedFake = true;
    private final Context mContext;
    protected HashMap<Integer, PerformanceStats> mCryptoPerformanceMap = new HashMap<>();
    /* access modifiers changed from: private */
    public ClientMonitor mCurrentClient;
    protected int mCurrentUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
    private IHwBinder mExtDaemon;
    private final Object mFodLock = new Object();
    private IBinder mFodService;
    protected int mHALDeathCount;
    protected long mHalDeviceId;
    protected final H mHandler = new H();
    protected boolean mIsCrypto;
    private final String mKeyguardPackage;
    private final ArrayList<LockoutResetMonitor> mLockoutMonitors = new ArrayList<>();
    private final MetricsLogger mMetricsLogger;
    /* access modifiers changed from: private */
    public ClientMonitor mPendingClient;
    protected HashMap<Integer, PerformanceStats> mPerformanceMap = new HashMap<>();
    /* access modifiers changed from: private */
    public PerformanceStats mPerformanceStats;
    /* access modifiers changed from: private */
    public final PowerManager mPowerManager;
    private ClientMonitor mPreviousClient;
    protected int mRequestId = 0;
    private final ResetClientStateRunnable mResetClientState = new ResetClientStateRunnable();
    protected int mRootUserId = 0;
    protected final IStatusBarService mStatusBarService;
    /* access modifiers changed from: private */
    public final BiometricTaskStackListener mTaskStackListener = new BiometricTaskStackListener();
    private final IBinder mToken = new Binder();
    private final ArrayList<UserTemplate> mUnknownHALTemplates = new ArrayList<>();
    private final UserManager mUserManager;
    private WindowManagerPolicy mWindowManagerPolicy;

    protected interface DaemonWrapper {
        public static final int ERROR_ESRCH = 3;

        int authenticate(long j, int i) throws RemoteException;

        int cancel() throws RemoteException;

        int enroll(byte[] bArr, int i, int i2, ArrayList<Integer> arrayList) throws RemoteException;

        int enumerate() throws RemoteException;

        int remove(int i, int i2) throws RemoteException;

        void resetLockout(byte[] bArr) throws RemoteException;
    }

    /* access modifiers changed from: protected */
    public abstract boolean checkAppOps(int i, String str);

    /* access modifiers changed from: protected */
    public abstract void checkUseBiometricPermission();

    /* access modifiers changed from: protected */
    public abstract BiometricUtils getBiometricUtils();

    /* access modifiers changed from: protected */
    public abstract Constants getConstants();

    /* access modifiers changed from: protected */
    public abstract DaemonWrapper getDaemonWrapper();

    /* access modifiers changed from: protected */
    public abstract List<? extends BiometricAuthenticator.Identifier> getEnrolledTemplates(int i);

    /* access modifiers changed from: protected */
    public abstract long getHalDeviceId();

    /* access modifiers changed from: protected */
    public abstract String getLockoutBroadcastPermission();

    /* access modifiers changed from: protected */
    public abstract int getLockoutMode();

    /* access modifiers changed from: protected */
    public abstract String getLockoutResetIntent();

    /* access modifiers changed from: protected */
    public abstract String getManageBiometricPermission();

    /* access modifiers changed from: protected */
    public abstract String getTag();

    /* access modifiers changed from: protected */
    public abstract boolean hasEnrolledBiometrics(int i);

    /* access modifiers changed from: protected */
    public abstract boolean hasReachedEnrollmentLimit(int i);

    /* access modifiers changed from: protected */
    public abstract int statsModality();

    /* access modifiers changed from: protected */
    public abstract void updateActiveGroup(int i, String str);

    protected class PerformanceStats {
        public int accept;
        public int acquire;
        public int lockout;
        public int permanentLockout;
        public int reject;

        protected PerformanceStats() {
        }
    }

    /* access modifiers changed from: protected */
    public void notifyClientActiveCallbacks(boolean isActive) {
    }

    protected abstract class AuthenticationClientImpl extends AuthenticationClient {
        /* access modifiers changed from: protected */
        public boolean isFingerprint() {
            return false;
        }

        public AuthenticationClientImpl(Context context, DaemonWrapper daemon, long halDeviceId, IBinder token, ServiceListener listener, int targetUserId, int groupId, long opId, boolean restricted, String owner, int cookie, boolean requireConfirmation) {
            super(context, BiometricServiceBase.this.getConstants(), daemon, halDeviceId, token, listener, targetUserId, groupId, opId, restricted, owner, cookie, requireConfirmation);
        }

        /* access modifiers changed from: protected */
        public int statsClient() {
            if (BiometricServiceBase.this.isKeyguard(getOwnerString())) {
                return 1;
            }
            if (isBiometricPrompt()) {
                return 2;
            }
            if (isFingerprint()) {
                return 3;
            }
            return 0;
        }

        public void onStart() {
            try {
                BiometricServiceBase.this.mActivityTaskManager.registerTaskStackListener(BiometricServiceBase.this.mTaskStackListener);
            } catch (RemoteException e) {
                Slog.e(BiometricServiceBase.this.getTag(), "Could not register task stack listener", e);
            }
        }

        public void onStop() {
            try {
                BiometricServiceBase.this.mActivityTaskManager.unregisterTaskStackListener(BiometricServiceBase.this.mTaskStackListener);
            } catch (RemoteException e) {
                Slog.e(BiometricServiceBase.this.getTag(), "Could not unregister task stack listener", e);
            }
        }

        public void notifyUserActivity() {
            BiometricServiceBase.this.userActivity();
        }

        public int handleFailedAttempt() {
            int lockoutMode = BiometricServiceBase.this.getLockoutMode();
            if (lockoutMode == 2) {
                BiometricServiceBase.this.mPerformanceStats.permanentLockout++;
            } else if (lockoutMode == 1) {
                BiometricServiceBase.this.mPerformanceStats.lockout++;
            }
            if (lockoutMode == 0) {
                return 0;
            }
            BiometricServiceBase.this.fodCallBack(5, lockoutMode);
            return lockoutMode;
        }
    }

    protected abstract class EnrollClientImpl extends EnrollClient {
        public EnrollClientImpl(Context context, DaemonWrapper daemon, long halDeviceId, IBinder token, ServiceListener listener, int userId, int groupId, byte[] cryptoToken, boolean restricted, String owner, int[] disabledFeatures) {
            super(context, BiometricServiceBase.this.getConstants(), daemon, halDeviceId, token, listener, userId, groupId, cryptoToken, restricted, owner, BiometricServiceBase.this.getBiometricUtils(), disabledFeatures);
        }

        public void notifyUserActivity() {
            BiometricServiceBase.this.userActivity();
        }
    }

    private final class InternalRemovalClient extends RemovalClient {
        InternalRemovalClient(Context context, DaemonWrapper daemon, long halDeviceId, IBinder token, ServiceListener listener, int templateId, int groupId, int userId, boolean restricted, String owner) {
            super(context, BiometricServiceBase.this.getConstants(), daemon, halDeviceId, token, listener, templateId, groupId, userId, restricted, owner, BiometricServiceBase.this.getBiometricUtils());
        }

        /* access modifiers changed from: protected */
        public int statsModality() {
            return BiometricServiceBase.this.statsModality();
        }
    }

    private final class InternalEnumerateClient extends EnumerateClient {
        private List<? extends BiometricAuthenticator.Identifier> mEnrolledList;
        private List<BiometricAuthenticator.Identifier> mUnknownHALTemplates = new ArrayList();
        private BiometricUtils mUtils;
        final /* synthetic */ BiometricServiceBase this$0;

        /* JADX INFO: super call moved to the top of the method (can break code semantics) */
        InternalEnumerateClient(BiometricServiceBase biometricServiceBase, Context context, DaemonWrapper daemon, long halDeviceId, IBinder token, ServiceListener listener, int groupId, int userId, boolean restricted, String owner, List<? extends BiometricAuthenticator.Identifier> enrolledList, BiometricUtils utils) {
            super(context, biometricServiceBase.getConstants(), daemon, halDeviceId, token, listener, groupId, userId, restricted, owner);
            this.this$0 = biometricServiceBase;
            this.mEnrolledList = enrolledList;
            this.mUtils = utils;
        }

        private void handleEnumeratedTemplate(BiometricAuthenticator.Identifier identifier) {
            if (identifier != null) {
                String tag = this.this$0.getTag();
                Slog.v(tag, "handleEnumeratedTemplate: " + identifier.getBiometricId());
                boolean matched = false;
                int i = 0;
                while (true) {
                    if (i >= this.mEnrolledList.size()) {
                        break;
                    } else if (this.mEnrolledList.get(i).getBiometricId() == identifier.getBiometricId()) {
                        this.mEnrolledList.remove(i);
                        matched = true;
                        break;
                    } else {
                        i++;
                    }
                }
                if (!matched && identifier.getBiometricId() != 0) {
                    this.mUnknownHALTemplates.add(identifier);
                }
                String tag2 = this.this$0.getTag();
                Slog.v(tag2, "Matched: " + matched);
            }
        }

        private void doTemplateCleanup() {
            if (this.mEnrolledList != null) {
                for (int i = 0; i < this.mEnrolledList.size(); i++) {
                    BiometricAuthenticator.Identifier identifier = this.mEnrolledList.get(i);
                    String tag = this.this$0.getTag();
                    Slog.e(tag, "doTemplateCleanup(): Removing dangling template from framework: " + identifier.getBiometricId() + " " + identifier.getName());
                    this.mUtils.removeBiometricForUser(getContext(), getTargetUserId(), identifier.getBiometricId());
                    StatsLog.write(148, statsModality(), 2);
                }
                this.mEnrolledList.clear();
            }
        }

        public List<BiometricAuthenticator.Identifier> getUnknownHALTemplates() {
            return this.mUnknownHALTemplates;
        }

        public boolean onEnumerationResult(BiometricAuthenticator.Identifier identifier, int remaining) {
            handleEnumeratedTemplate(identifier);
            if (remaining == 0) {
                doTemplateCleanup();
            }
            return remaining == 0;
        }

        /* access modifiers changed from: protected */
        public int statsModality() {
            return this.this$0.statsModality();
        }
    }

    protected interface ServiceListener {
        void onAcquired(long j, int i, int i2) throws RemoteException;

        void onError(long j, int i, int i2, int i3) throws RemoteException;

        void onEnrollResult(BiometricAuthenticator.Identifier identifier, int remaining) throws RemoteException {
        }

        void onAuthenticationSucceeded(long deviceId, BiometricAuthenticator.Identifier biometric, int userId) throws RemoteException {
            throw new UnsupportedOperationException("Stub!");
        }

        void onAuthenticationSucceededInternal(boolean requireConfirmation, byte[] token) throws RemoteException {
            throw new UnsupportedOperationException("Stub!");
        }

        void onAuthenticationFailed(long deviceId) throws RemoteException {
            throw new UnsupportedOperationException("Stub!");
        }

        void onAuthenticationFailedInternal(int cookie, boolean requireConfirmation) throws RemoteException {
            throw new UnsupportedOperationException("Stub!");
        }

        void onRemoved(BiometricAuthenticator.Identifier identifier, int remaining) throws RemoteException {
        }

        void onEnumerated(BiometricAuthenticator.Identifier identifier, int remaining) throws RemoteException {
        }
    }

    protected abstract class BiometricServiceListener implements ServiceListener {
        private IBiometricServiceReceiverInternal mWrapperReceiver;

        public BiometricServiceListener(IBiometricServiceReceiverInternal wrapperReceiver) {
            this.mWrapperReceiver = wrapperReceiver;
        }

        public IBiometricServiceReceiverInternal getWrapperReceiver() {
            return this.mWrapperReceiver;
        }

        public void onAuthenticationSucceededInternal(boolean requireConfirmation, byte[] token) throws RemoteException {
            if (getWrapperReceiver() != null) {
                getWrapperReceiver().onAuthenticationSucceeded(requireConfirmation, token);
            }
        }

        public void onAuthenticationFailedInternal(int cookie, boolean requireConfirmation) throws RemoteException {
            if (getWrapperReceiver() != null) {
                getWrapperReceiver().onAuthenticationFailed(cookie, requireConfirmation);
            }
        }
    }

    protected final class H extends Handler {
        protected H() {
        }

        public void handleMessage(Message msg) {
            if (msg.what != 10) {
                String tag = BiometricServiceBase.this.getTag();
                Slog.w(tag, "Unknown message:" + msg.what);
                return;
            }
            BiometricServiceBase.this.handleUserSwitching(msg.arg1);
        }
    }

    private final class BiometricTaskStackListener extends TaskStackListener {
        private BiometricTaskStackListener() {
        }

        public void onTaskStackChanged() {
            BiometricServiceBase.this.mHandler.post(new Runnable() {
                public void run() {
                    try {
                        if (BiometricServiceBase.this.mCurrentClient instanceof AuthenticationClient) {
                            String currentClient = BiometricServiceBase.this.mCurrentClient.getOwnerString();
                            if (!BiometricServiceBase.this.isKeyguard(currentClient)) {
                                List<ActivityManager.RunningTaskInfo> runningTasks = BiometricServiceBase.this.mActivityTaskManager.getTasks(1);
                                if (!runningTasks.isEmpty()) {
                                    String topPackage = runningTasks.get(0).topActivity.getPackageName();
                                    if (!topPackage.contentEquals(currentClient) && !BiometricServiceBase.this.mCurrentClient.isAlreadyDone()) {
                                        String tag = BiometricServiceBase.this.getTag();
                                        Slog.e(tag, "Stopping background authentication, top: " + topPackage + " currentClient: " + currentClient);
                                        BiometricServiceBase.this.fodCallBack(6, 0);
                                        BiometricServiceBase.this.mCurrentClient.stop(false);
                                    }
                                }
                            }
                        }
                    } catch (RemoteException e) {
                        Slog.e(BiometricServiceBase.this.getTag(), "Unable to get running tasks", e);
                    }
                }
            });
        }
    }

    private final class ResetClientStateRunnable implements Runnable {
        private ResetClientStateRunnable() {
        }

        public void run() {
            String tag = BiometricServiceBase.this.getTag();
            StringBuilder sb = new StringBuilder();
            sb.append("Client ");
            String str = "null";
            sb.append(BiometricServiceBase.this.mCurrentClient != null ? BiometricServiceBase.this.mCurrentClient.getOwnerString() : str);
            sb.append(" failed to respond to cancel, starting client ");
            if (BiometricServiceBase.this.mPendingClient != null) {
                str = BiometricServiceBase.this.mPendingClient.getOwnerString();
            }
            sb.append(str);
            Slog.w(tag, sb.toString());
            StatsLog.write(148, BiometricServiceBase.this.statsModality(), 4);
            ClientMonitor unused = BiometricServiceBase.this.mCurrentClient = null;
            BiometricServiceBase biometricServiceBase = BiometricServiceBase.this;
            biometricServiceBase.startClient(biometricServiceBase.mPendingClient, false);
        }
    }

    private final class LockoutResetMonitor implements IBinder.DeathRecipient {
        private static final long WAKELOCK_TIMEOUT_MS = 2000;
        private final IBiometricServiceLockoutResetCallback mCallback;
        private final Runnable mRemoveCallbackRunnable = new Runnable() {
            public void run() {
                LockoutResetMonitor.this.releaseWakelock();
                BiometricServiceBase.this.removeLockoutResetCallback(LockoutResetMonitor.this);
            }
        };
        private final PowerManager.WakeLock mWakeLock;

        public LockoutResetMonitor(IBiometricServiceLockoutResetCallback callback) {
            this.mCallback = callback;
            this.mWakeLock = BiometricServiceBase.this.mPowerManager.newWakeLock(1, "lockout reset callback");
            try {
                this.mCallback.asBinder().linkToDeath(this, 0);
            } catch (RemoteException e) {
                Slog.w(BiometricServiceBase.this.getTag(), "caught remote exception in linkToDeath", e);
            }
        }

        public void sendLockoutReset() {
            if (this.mCallback != null) {
                try {
                    this.mWakeLock.acquire(WAKELOCK_TIMEOUT_MS);
                    this.mCallback.onLockoutReset(BiometricServiceBase.this.getHalDeviceId(), new IRemoteCallback.Stub() {
                        public void sendResult(Bundle data) throws RemoteException {
                            LockoutResetMonitor.this.releaseWakelock();
                        }
                    });
                } catch (DeadObjectException e) {
                    Slog.w(BiometricServiceBase.this.getTag(), "Death object while invoking onLockoutReset: ", e);
                    BiometricServiceBase.this.mHandler.post(this.mRemoveCallbackRunnable);
                } catch (RemoteException e2) {
                    Slog.w(BiometricServiceBase.this.getTag(), "Failed to invoke onLockoutReset: ", e2);
                    releaseWakelock();
                }
            }
        }

        public void binderDied() {
            Slog.e(BiometricServiceBase.this.getTag(), "Lockout reset callback binder died");
            BiometricServiceBase.this.mHandler.post(this.mRemoveCallbackRunnable);
        }

        /* access modifiers changed from: private */
        public void releaseWakelock() {
            if (this.mWakeLock.isHeld()) {
                this.mWakeLock.release();
            }
        }
    }

    private final class UserTemplate {
        final BiometricAuthenticator.Identifier mIdentifier;
        final int mUserId;

        UserTemplate(BiometricAuthenticator.Identifier identifier, int userId) {
            this.mIdentifier = identifier;
            this.mUserId = userId;
        }
    }

    public BiometricServiceBase(Context context) {
        super(context);
        this.mContext = context;
        this.mStatusBarService = IStatusBarService.Stub.asInterface(ServiceManager.getService("statusbar"));
        this.mKeyguardPackage = ComponentName.unflattenFromString(context.getResources().getString(17039771)).getPackageName();
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        ActivityTaskManager activityTaskManager = (ActivityTaskManager) context.getSystemService("activity_task");
        this.mActivityTaskManager = ActivityTaskManager.getService();
        this.mPowerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
        this.mWindowManagerPolicy = (WindowManagerPolicy) LocalServices.getService(WindowManagerPolicy.class);
        this.mUserManager = UserManager.get(this.mContext);
        this.mMetricsLogger = new MetricsLogger();
    }

    public void onStart() {
        listenForUserSwitches();
    }

    public void serviceDied(long cookie) {
        Slog.e(getTag(), "HAL died");
        this.mMetricsLogger.count(getConstants().tagHalDied(), 1);
        this.mHALDeathCount++;
        this.mCurrentUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        handleError(getHalDeviceId(), 1, 0);
        StatsLog.write(148, statsModality(), 1);
    }

    /* access modifiers changed from: protected */
    public ClientMonitor getCurrentClient() {
        return this.mCurrentClient;
    }

    /* access modifiers changed from: protected */
    public ClientMonitor getPendingClient() {
        return this.mPendingClient;
    }

    /* access modifiers changed from: protected */
    public void handleAcquired(long deviceId, int acquiredInfo, int vendorCode) {
        FingerprintServiceInjector.recordAcquiredInfo(acquiredInfo, vendorCode);
        ClientMonitor client = this.mCurrentClient;
        if (client != null && client.onAcquired(acquiredInfo, vendorCode)) {
            removeClient(client);
        }
        if (this.mPerformanceStats != null && getLockoutMode() == 0 && (client instanceof AuthenticationClient)) {
            this.mPerformanceStats.acquire++;
        }
    }

    /* access modifiers changed from: protected */
    public void handleAuthenticated(BiometricAuthenticator.Identifier identifier, ArrayList<Byte> token) {
        fodCallBack(3, identifier.getBiometricId());
        ClientMonitor client = this.mCurrentClient;
        boolean authenticated = identifier.getBiometricId() != 0;
        if (identifier.getBiometricId() != 0) {
            byte[] byteToken = new byte[token.size()];
            for (int i = 0; i < token.size(); i++) {
                byteToken[i] = token.get(i).byteValue();
            }
            KeyStore.getInstance().addAuthToken(byteToken);
        }
        if (client != null && client.onAuthenticated(identifier, authenticated, token)) {
            removeClient(client);
        }
        if (authenticated) {
            this.mPerformanceStats.accept++;
            return;
        }
        this.mPerformanceStats.reject++;
    }

    /* access modifiers changed from: protected */
    public void handleEnrollResult(BiometricAuthenticator.Identifier identifier, int remaining) {
        fodCallBack(9, remaining);
        ClientMonitor client = this.mCurrentClient;
        if (client != null && client.onEnrollResult(identifier, remaining)) {
            removeClient(client);
            if (identifier instanceof Fingerprint) {
                updateActiveGroup(((Fingerprint) identifier).getGroupId(), (String) null);
            } else {
                updateActiveGroup(this.mCurrentUserId, (String) null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleError(long deviceId, int error, int vendorCode) {
        fodCallBack(4, error);
        ClientMonitor client = this.mCurrentClient;
        String tag = getTag();
        StringBuilder sb = new StringBuilder();
        sb.append("handleError(client=");
        sb.append(client != null ? client.getOwnerString() : "null");
        sb.append(", error = ");
        sb.append(error);
        sb.append(")");
        Slog.v(tag, sb.toString());
        if ((client instanceof InternalRemovalClient) || (client instanceof InternalEnumerateClient)) {
            clearEnumerateState();
        }
        if (client != null && client.onError(deviceId, error, vendorCode)) {
            removeClient(client);
        }
        if (error == 5) {
            this.mHandler.removeCallbacks(this.mResetClientState);
            if (this.mPendingClient != null) {
                String tag2 = getTag();
                Slog.v(tag2, "start pending client " + this.mPendingClient.getOwnerString());
                startClient(this.mPendingClient, false);
                this.mPendingClient = null;
            }
        } else if (error == 1) {
            Slog.w(getTag(), "Got ERROR_HW_UNAVAILABLE; try reconnecting next client.");
            synchronized (this) {
                this.mPreviousClient = null;
                this.mHalDeviceId = 0;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleRemoved(BiometricAuthenticator.Identifier identifier, int remaining) {
        String tag = getTag();
        Slog.w(tag, "Removed: fid=" + identifier.getBiometricId() + ", dev=" + identifier.getDeviceId() + ", rem=" + remaining);
        ClientMonitor client = this.mCurrentClient;
        if (client != null && client.onRemoved(identifier, remaining)) {
            removeClient(client);
            int userId = this.mCurrentUserId;
            if (identifier instanceof Fingerprint) {
                userId = ((Fingerprint) identifier).getGroupId();
            }
            if (!hasEnrolledBiometrics(userId)) {
                updateActiveGroup(userId, (String) null);
            }
        }
        if ((client instanceof InternalRemovalClient) != 0 && !this.mUnknownHALTemplates.isEmpty()) {
            startCleanupUnknownHALTemplates();
        } else if (client instanceof InternalRemovalClient) {
            clearEnumerateState();
        }
    }

    /* access modifiers changed from: protected */
    public void handleEnumerate(BiometricAuthenticator.Identifier identifier, int remaining) {
        ClientMonitor client = getCurrentClient();
        if ((client instanceof InternalRemovalClient) || (client instanceof EnumerateClient)) {
            client.onEnumerationResult(identifier, remaining);
            if (remaining != 0) {
                return;
            }
            if (client instanceof InternalEnumerateClient) {
                List<BiometricAuthenticator.Identifier> unknownHALTemplates = ((InternalEnumerateClient) client).getUnknownHALTemplates();
                if (!unknownHALTemplates.isEmpty()) {
                    String tag = getTag();
                    Slog.w(tag, "Adding " + unknownHALTemplates.size() + " templates for deletion");
                }
                for (int i = 0; i < unknownHALTemplates.size(); i++) {
                    this.mUnknownHALTemplates.add(new UserTemplate(unknownHALTemplates.get(i), client.getTargetUserId()));
                }
                removeClient(client);
                startCleanupUnknownHALTemplates();
                return;
            }
            removeClient(client);
        }
    }

    /* access modifiers changed from: protected */
    public void enrollInternal(EnrollClientImpl client, int userId) {
        if (!hasReachedEnrollmentLimit(userId) && isCurrentUserOrProfile(userId)) {
            this.mHandler.post(new Runnable(client) {
                private final /* synthetic */ BiometricServiceBase.EnrollClientImpl f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    BiometricServiceBase.this.lambda$enrollInternal$0$BiometricServiceBase(this.f$1);
                }
            });
        }
    }

    public /* synthetic */ void lambda$enrollInternal$0$BiometricServiceBase(EnrollClientImpl client) {
        startClient(client, true);
    }

    /* access modifiers changed from: protected */
    public void cancelEnrollmentInternal(IBinder token) {
        this.mHandler.post(new Runnable(token) {
            private final /* synthetic */ IBinder f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BiometricServiceBase.this.lambda$cancelEnrollmentInternal$1$BiometricServiceBase(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$cancelEnrollmentInternal$1$BiometricServiceBase(IBinder token) {
        ClientMonitor client = this.mCurrentClient;
        if ((client instanceof EnrollClient) && client.getToken() == token) {
            Slog.v(getTag(), "Cancelling enrollment");
            boolean z = false;
            fodCallBack(8, 0);
            if (client.getToken() == token) {
                z = true;
            }
            client.stop(z);
        }
    }

    /* access modifiers changed from: protected */
    public void authenticateInternal(AuthenticationClientImpl client, long opId, String opPackageName) {
        authenticateInternal(client, opId, opPackageName, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId());
    }

    /* access modifiers changed from: protected */
    public void authenticateInternal(AuthenticationClientImpl client, long opId, String opPackageName, int callingUid, int callingPid, int callingUserId) {
        if (!canUseBiometric(opPackageName, true, callingUid, callingPid, callingUserId)) {
            String tag = getTag();
            Slog.v(tag, "authenticate(): reject " + opPackageName);
            return;
        }
        this.mHandler.post(new Runnable(opPackageName, opId, client) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ long f$2;
            private final /* synthetic */ BiometricServiceBase.AuthenticationClientImpl f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r5;
            }

            public final void run() {
                BiometricServiceBase.this.lambda$authenticateInternal$2$BiometricServiceBase(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$authenticateInternal$2$BiometricServiceBase(String opPackageName, long opId, AuthenticationClientImpl client) {
        if (this.mCurrentClient == null || this.mKeyguardPackage.equals(opPackageName) || !this.mKeyguardPackage.equals(this.mCurrentClient.getOwnerString())) {
            FingerprintServiceInjector.initAcquiredInfo();
            boolean z = true;
            this.mMetricsLogger.histogram(getConstants().tagAuthToken(), opId != 0 ? 1 : 0);
            HashMap<Integer, PerformanceStats> pmap = opId == 0 ? this.mPerformanceMap : this.mCryptoPerformanceMap;
            PerformanceStats stats = pmap.get(Integer.valueOf(this.mCurrentUserId));
            if (stats == null) {
                stats = new PerformanceStats();
                pmap.put(Integer.valueOf(this.mCurrentUserId), stats);
            }
            this.mPerformanceStats = stats;
            if (opId == 0) {
                z = false;
            }
            this.mIsCrypto = z;
            startAuthentication(client, opPackageName);
            return;
        }
        Slog.v(getTag(), "authenticate(): reject " + opPackageName + "; may preemption " + this.mCurrentClient.getOwnerString());
    }

    /* access modifiers changed from: protected */
    public void cancelAuthenticationInternal(IBinder token, String opPackageName) {
        cancelAuthenticationInternal(token, opPackageName, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId(), true);
    }

    /* access modifiers changed from: protected */
    public void cancelAuthenticationInternal(IBinder token, String opPackageName, int callingUid, int callingPid, int callingUserId, boolean fromClient) {
        if (!fromClient || canUseBiometric(opPackageName, true, callingUid, callingPid, callingUserId)) {
            this.mHandler.post(new Runnable(token, fromClient) {
                private final /* synthetic */ IBinder f$1;
                private final /* synthetic */ boolean f$2;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                }

                public final void run() {
                    BiometricServiceBase.this.lambda$cancelAuthenticationInternal$3$BiometricServiceBase(this.f$1, this.f$2);
                }
            });
            return;
        }
        String tag = getTag();
        Slog.v(tag, "cancelAuthentication(): reject " + opPackageName);
    }

    public /* synthetic */ void lambda$cancelAuthenticationInternal$3$BiometricServiceBase(IBinder token, boolean fromClient) {
        ClientMonitor client = this.mCurrentClient;
        if (client instanceof AuthenticationClient) {
            if (client.getToken() == token || !fromClient) {
                String tag = getTag();
                Slog.v(tag, "Stopping client " + client.getOwnerString() + ", fromClient: " + fromClient);
                boolean z = false;
                fodCallBack(2, 0);
                if (client.getToken() == token) {
                    z = true;
                }
                client.stop(z);
                return;
            }
            String tag2 = getTag();
            Slog.v(tag2, "Can't stop client " + client.getOwnerString() + " since tokens don't match. fromClient: " + fromClient);
        } else if (client != null) {
            String tag3 = getTag();
            Slog.v(tag3, "Can't cancel non-authenticating client " + client.getOwnerString());
        }
    }

    /* access modifiers changed from: protected */
    public void setActiveUserInternal(int userId) {
        updateActiveGroup(userId, (String) null);
    }

    /* access modifiers changed from: protected */
    public void removeInternal(RemovalClient client) {
        this.mHandler.post(new Runnable(client) {
            private final /* synthetic */ RemovalClient f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BiometricServiceBase.this.lambda$removeInternal$4$BiometricServiceBase(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeInternal$4$BiometricServiceBase(RemovalClient client) {
        startClient(client, true);
    }

    /* access modifiers changed from: protected */
    public void enumerateInternal(EnumerateClient client) {
        this.mHandler.post(new Runnable(client) {
            private final /* synthetic */ EnumerateClient f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BiometricServiceBase.this.lambda$enumerateInternal$5$BiometricServiceBase(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$enumerateInternal$5$BiometricServiceBase(EnumerateClient client) {
        startClient(client, true);
    }

    private void startAuthentication(AuthenticationClientImpl client, String opPackageName) {
        int errorCode;
        String tag = getTag();
        Slog.v(tag, "startAuthentication(" + opPackageName + ")");
        int lockoutMode = getLockoutMode();
        if (lockoutMode != 0) {
            String tag2 = getTag();
            Slog.v(tag2, "In lockout mode(" + lockoutMode + ") ; disallowing authentication");
            if (lockoutMode == 1) {
                errorCode = 7;
            } else {
                errorCode = 9;
            }
            if (!client.onError(getHalDeviceId(), errorCode, 0)) {
                Slog.w(getTag(), "Cannot send permanent lockout message to client");
                return;
            }
            return;
        }
        startClient(client, true);
    }

    /* access modifiers changed from: protected */
    public void addLockoutResetCallback(IBiometricServiceLockoutResetCallback callback) {
        this.mHandler.post(new Runnable(callback) {
            private final /* synthetic */ IBiometricServiceLockoutResetCallback f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                BiometricServiceBase.this.lambda$addLockoutResetCallback$6$BiometricServiceBase(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$addLockoutResetCallback$6$BiometricServiceBase(IBiometricServiceLockoutResetCallback callback) {
        LockoutResetMonitor monitor = new LockoutResetMonitor(callback);
        if (!this.mLockoutMonitors.contains(monitor)) {
            this.mLockoutMonitors.add(monitor);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canUseBiometric(String opPackageName, boolean requireForeground, int uid, int pid, int userId) {
        checkUseBiometricPermission();
        if (Binder.getCallingUid() == 1000 || isKeyguard(opPackageName)) {
            return true;
        }
        if (!isCurrentUserOrProfile(userId)) {
            String tag = getTag();
            Slog.w(tag, "Rejecting " + opPackageName + "; not a current user or profile");
            return false;
        } else if (!checkAppOps(uid, opPackageName)) {
            String tag2 = getTag();
            Slog.w(tag2, "Rejecting " + opPackageName + "; permission denied");
            return false;
        } else if (!requireForeground || isForegroundActivity(uid, pid) || isCurrentClient(opPackageName)) {
            return true;
        } else {
            String tag3 = getTag();
            Slog.w(tag3, "Rejecting " + opPackageName + "; not in foreground");
            return false;
        }
    }

    private boolean isCurrentClient(String opPackageName) {
        ClientMonitor clientMonitor = this.mCurrentClient;
        return clientMonitor != null && clientMonitor.getOwnerString().equals(opPackageName);
    }

    /* access modifiers changed from: private */
    public boolean isKeyguard(String clientPackage) {
        return this.mKeyguardPackage.equals(clientPackage);
    }

    private boolean isForegroundActivity(int uid, int pid) {
        try {
            List<ActivityManager.RunningAppProcessInfo> procs = ActivityManager.getService().getRunningAppProcesses();
            int N = procs.size();
            for (int i = 0; i < N; i++) {
                ActivityManager.RunningAppProcessInfo proc = procs.get(i);
                if (proc.pid == pid && proc.uid == uid && proc.importance <= 125) {
                    return true;
                }
            }
            return false;
        } catch (RemoteException e) {
            Slog.w(getTag(), "am.getRunningAppProcesses() failed");
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void startClient(ClientMonitor newClient, boolean initiatedByClient) {
        ClientMonitor currentClient = this.mCurrentClient;
        if (currentClient != null) {
            if (currentClient.mAlreadyDone || !currentClient.getOwnerString().equals(newClient.getOwnerString())) {
                String tag = getTag();
                Slog.v(tag, "request stop current client " + currentClient.getOwnerString());
                if (!(currentClient instanceof InternalEnumerateClient) && !(currentClient instanceof InternalRemovalClient)) {
                    fodCallBack(6, 0);
                    currentClient.stop(initiatedByClient);
                    this.mHandler.removeCallbacks(this.mResetClientState);
                    this.mHandler.postDelayed(this.mResetClientState, 3000);
                } else if (newClient != null) {
                    String tag2 = getTag();
                    StringBuilder sb = new StringBuilder();
                    sb.append("Internal cleanup in progress but trying to start client ");
                    sb.append(newClient.getClass().getSuperclass().getSimpleName());
                    sb.append("(");
                    sb.append(newClient.getOwnerString());
                    sb.append("), initiatedByClient = ");
                    sb.append(initiatedByClient);
                    sb.append(" mPreviousClient: ");
                    ClientMonitor clientMonitor = this.mPreviousClient;
                    sb.append(clientMonitor == null ? null : clientMonitor.getOwnerString());
                    Slog.w(tag2, sb.toString());
                    if (this.mPreviousClient != null && newClient.getOwnerString().equals(this.mPreviousClient.getOwnerString())) {
                        String tag3 = getTag();
                        Slog.w(tag3, "start client " + this.mPreviousClient.getOwnerString() + " is disallowed.");
                        this.mPreviousClient = null;
                        return;
                    }
                }
                this.mPendingClient = newClient;
                return;
            }
            String tag4 = getTag();
            Slog.v(tag4, "previous client " + currentClient.getOwnerString() + " is not done, don't start again.");
        } else if (newClient != null) {
            if (newClient instanceof AuthenticationClient) {
                AuthenticationClient client = (AuthenticationClient) newClient;
                fodCallBack(1, client.mFlags, client.getOwnerString());
                if (client.isBiometricPrompt()) {
                    String tag5 = getTag();
                    Slog.v(tag5, "Returning cookie: " + client.getCookie());
                    this.mCurrentClient = newClient;
                    if (this.mBiometricService == null) {
                        this.mBiometricService = IBiometricService.Stub.asInterface(ServiceManager.getService("biometric"));
                    }
                    try {
                        this.mBiometricService.onReadyForAuthentication(client.getCookie(), client.getRequireConfirmation(), client.getTargetUserId());
                        return;
                    } catch (RemoteException e) {
                        Slog.e(getTag(), "Remote exception", e);
                        return;
                    }
                }
            } else if (newClient instanceof EnrollClient) {
                fodCallBack(7, 0);
            }
            this.mCurrentClient = newClient;
            startCurrentClient(this.mCurrentClient.getCookie());
        }
    }

    /* access modifiers changed from: protected */
    public void startCurrentClient(int cookie) {
        if (this.mCurrentClient == null) {
            Slog.e(getTag(), "Trying to start null client!");
            return;
        }
        String tag = getTag();
        StringBuilder sb = new StringBuilder();
        sb.append("starting client ");
        sb.append(this.mCurrentClient.getClass().getSuperclass().getSimpleName());
        sb.append("(");
        sb.append(this.mCurrentClient.getOwnerString());
        sb.append(") cookie: ");
        sb.append(cookie);
        sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
        sb.append(this.mCurrentClient.getCookie());
        sb.append(" mPreviousClient: ");
        ClientMonitor clientMonitor = this.mPreviousClient;
        sb.append(clientMonitor == null ? null : clientMonitor.getOwnerString());
        Slog.v(tag, sb.toString());
        if (cookie != this.mCurrentClient.getCookie()) {
            Slog.e(getTag(), "Mismatched cookie");
            return;
        }
        if (IS_FOD && isNightMode()) {
            this.mWindowManagerPolicy.notifyFpClientState(true, false);
        }
        notifyClientActiveCallbacks(true);
        this.mCurrentClient.start();
    }

    /* access modifiers changed from: protected */
    public void removeClient(ClientMonitor client) {
        if (client != null) {
            client.destroy();
            if (IS_FOD && isNightMode()) {
                this.mWindowManagerPolicy.notifyFpClientState(false, isKeyguard(client.getOwnerString()));
            }
            ClientMonitor clientMonitor = this.mCurrentClient;
            if (!(client == clientMonitor || clientMonitor == null)) {
                String tag = getTag();
                Slog.w(tag, "Unexpected client: " + client.getOwnerString() + "expected: " + this.mCurrentClient.getOwnerString());
            }
        }
        if (this.mCurrentClient != null) {
            String tag2 = getTag();
            Slog.v(tag2, "Done with client: " + client.getOwnerString());
            this.mPreviousClient = this.mCurrentClient;
            this.mCurrentClient = null;
        }
        if (this.mPendingClient == null) {
            notifyClientActiveCallbacks(false);
        }
    }

    /* access modifiers changed from: protected */
    public void loadAuthenticatorIds() {
        long t = System.currentTimeMillis();
        this.mAuthenticatorIds.clear();
        for (UserInfo user : UserManager.get(getContext()).getUsers(true)) {
            int userId = getUserOrWorkProfileId((String) null, user.id);
            if (!this.mAuthenticatorIds.containsKey(Integer.valueOf(userId))) {
                updateActiveGroup(userId, (String) null);
            }
        }
        long t2 = System.currentTimeMillis() - t;
        if (t2 > 1000) {
            String tag = getTag();
            Slog.w(tag, "loadAuthenticatorIds() taking too long: " + t2 + "ms");
        }
    }

    /* access modifiers changed from: protected */
    public int getUserOrWorkProfileId(String clientPackage, int userId) {
        return this.mRootUserId;
    }

    /* access modifiers changed from: protected */
    public boolean isRestricted() {
        return !hasPermission(getManageBiometricPermission());
    }

    /* access modifiers changed from: protected */
    public boolean hasPermission(String permission) {
        return getContext().checkCallingOrSelfPermission(permission) == 0;
    }

    /* access modifiers changed from: protected */
    public void checkPermission(String permission) {
        Context context = getContext();
        context.enforceCallingOrSelfPermission(permission, "Must have " + permission + " permission.");
    }

    /* access modifiers changed from: protected */
    public boolean isCurrentUserOrProfile(int userId) {
        return true;
    }

    /* access modifiers changed from: protected */
    public long getAuthenticatorId(String opPackageName) {
        return this.mAuthenticatorIds.getOrDefault(Integer.valueOf(getUserOrWorkProfileId(opPackageName, UserHandle.getCallingUserId())), 0L).longValue();
    }

    /* access modifiers changed from: protected */
    public void doTemplateCleanupForUser(int userId) {
        enumerateUser(userId);
    }

    private void clearEnumerateState() {
        Slog.v(getTag(), "clearEnumerateState()");
        this.mUnknownHALTemplates.clear();
    }

    private void startCleanupUnknownHALTemplates() {
        if (!this.mUnknownHALTemplates.isEmpty()) {
            UserTemplate template = this.mUnknownHALTemplates.get(0);
            this.mUnknownHALTemplates.remove(template);
            removeInternal(new InternalRemovalClient(getContext(), getDaemonWrapper(), this.mHalDeviceId, this.mToken, (ServiceListener) null, template.mIdentifier.getBiometricId(), 0, template.mUserId, !hasPermission(getManageBiometricPermission()), getContext().getPackageName()));
            StatsLog.write(148, statsModality(), 3);
            return;
        }
        clearEnumerateState();
        if (this.mPendingClient != null) {
            Slog.d(getTag(), "Enumerate finished, starting pending client");
            startClient(this.mPendingClient, false);
            this.mPendingClient = null;
        }
    }

    private void enumerateUser(int userId) {
        String tag = getTag();
        Slog.v(tag, "Enumerating user(" + userId + ")");
        List<? extends BiometricAuthenticator.Identifier> enrolledList = getEnrolledTemplates(userId);
        enumerateInternal(new InternalEnumerateClient(this, getContext(), getDaemonWrapper(), this.mHalDeviceId, this.mToken, (ServiceListener) null, userId, userId, hasPermission(getManageBiometricPermission()) ^ true, getContext().getOpPackageName(), enrolledList, getBiometricUtils()));
    }

    /* access modifiers changed from: protected */
    public void handleUserSwitching(int userId) {
        if (!this.mConfigNeedFake || this.mRootUserId == userId) {
            if ((getCurrentClient() instanceof InternalRemovalClient) || (getCurrentClient() instanceof InternalEnumerateClient)) {
                Slog.w(getTag(), "User switched while performing cleanup");
            }
            updateActiveGroup(userId, (String) null);
            doTemplateCleanupForUser(userId);
            return;
        }
        String tag = getTag();
        Slog.w(tag, "do not handleUserSwitching id: " + userId);
    }

    /* access modifiers changed from: protected */
    public void notifyLockoutResetMonitors() {
        for (int i = 0; i < this.mLockoutMonitors.size(); i++) {
            this.mLockoutMonitors.get(i).sendLockoutReset();
        }
    }

    /* access modifiers changed from: private */
    public void userActivity() {
        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), 2, 0);
    }

    private boolean isWorkProfile(int userId) {
        long token = Binder.clearCallingIdentity();
        try {
            UserInfo userInfo = this.mUserManager.getUserInfo(userId);
            return userInfo != null && userInfo.isManagedProfile();
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private int getEffectiveUserId(int userId) {
        UserManager um = UserManager.get(this.mContext);
        if (um != null) {
            long callingIdentity = Binder.clearCallingIdentity();
            int userId2 = um.getCredentialOwnerProfile(userId);
            Binder.restoreCallingIdentity(callingIdentity);
            return userId2;
        }
        Slog.e(getTag(), "Unable to acquire UserManager");
        return userId;
    }

    private void listenForUserSwitches() {
        try {
            ActivityManager.getService().registerUserSwitchObserver(new SynchronousUserSwitchObserver() {
                public void onUserSwitching(int newUserId) throws RemoteException {
                    BiometricServiceBase.this.mHandler.obtainMessage(10, newUserId, 0).sendToTarget();
                }
            }, getTag());
        } catch (RemoteException e) {
            Slog.w(getTag(), "Failed to listen for user switching event", e);
        }
    }

    /* access modifiers changed from: private */
    public void removeLockoutResetCallback(LockoutResetMonitor monitor) {
        this.mLockoutMonitors.remove(monitor);
    }

    /* access modifiers changed from: protected */
    public int startExtCmd(IBinder token, int groupId, int cmd, int param) {
        int result = -1;
        HwParcel hidl_reply = new HwParcel();
        try {
            if (this.mExtDaemon == null) {
                this.mExtDaemon = HwBinder.getService("vendor.xiaomi.hardware.fingerprintextension@1.0::IXiaomiFingerprint", BatteryService.HealthServiceWrapper.INSTANCE_VENDOR);
            }
            if (this.mExtDaemon == null) {
                Slog.e(getTag(), "startExtCmd: mExtDaemon service not found");
            } else {
                HwParcel hidl_request = new HwParcel();
                hidl_request.writeInterfaceToken("vendor.xiaomi.hardware.fingerprintextension@1.0::IXiaomiFingerprint");
                hidl_request.writeInt32(cmd);
                hidl_request.writeInt32(param);
                this.mExtDaemon.transact(1, hidl_request, hidl_reply, 0);
                hidl_reply.verifySuccess();
                hidl_request.releaseTemporaryStorage();
                result = hidl_reply.readInt32();
            }
        } catch (RemoteException e) {
            Slog.e(getTag(), "extCmd failed, reset mExtDaemon. ", e);
            this.mExtDaemon = null;
        } catch (Throwable hidl_request2) {
            hidl_reply.release();
            throw hidl_request2;
        }
        hidl_reply.release();
        String tag = getTag();
        Slog.i(tag, "startExtCmd cmd: " + cmd + " param: " + param + " result:" + result);
        return result;
    }

    /* access modifiers changed from: package-private */
    public int fodCallBack(int cmd, int param) {
        return fodCallBack(cmd, param, "");
    }

    /* access modifiers changed from: package-private */
    public int fodCallBack(int cmd, int param, String packName) {
        int resBack = -1;
        if (!IS_FOD) {
            return -1;
        }
        int status = getStatus4Touch(cmd, param);
        boolean touchFod = writeStatus4Touch(status);
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        try {
            IBinder remote = getFodServ();
            if (remote == null) {
                Slog.e(getTag(), "fodCallBack, service not found");
            } else {
                data.writeInterfaceToken("android.app.fod.ICallback");
                data.writeInt(cmd);
                data.writeInt(param);
                data.writeString(packName);
                remote.transact(1, data, reply, 0);
                reply.readException();
                resBack = reply.readInt();
            }
        } catch (RemoteException e) {
            String tag = getTag();
            Slog.e(tag, "fodCallBack failed, " + e);
        } catch (Throwable th) {
            data.recycle();
            reply.recycle();
            throw th;
        }
        data.recycle();
        reply.recycle();
        String tag2 = getTag();
        Slog.i(tag2, "fodCallBack cmd: " + getCmdStr(cmd) + " , " + param + " , " + packName + " backRes:" + resBack + " touchStatus:" + status + " writeRes:" + touchFod);
        return resBack;
    }

    private IBinder getFodServ() throws RemoteException {
        IBinder iBinder;
        synchronized (this.mFodLock) {
            if (this.mFodService == null) {
                this.mFodService = ServiceManager.getService("android.app.fod.ICallback");
                if (this.mFodService != null) {
                    this.mFodService.linkToDeath(new IBinder.DeathRecipient() {
                        public final void binderDied() {
                            BiometricServiceBase.this.lambda$getFodServ$7$BiometricServiceBase();
                        }
                    }, 0);
                }
            }
            iBinder = this.mFodService;
        }
        return iBinder;
    }

    public /* synthetic */ void lambda$getFodServ$7$BiometricServiceBase() {
        synchronized (this.mFodLock) {
            Slog.e(getTag(), "fodCallBack Service Died.");
            this.mFodService = null;
        }
    }

    private boolean writeStatus4Touch(int status) {
        boolean res = false;
        FileOutputStream fos = null;
        try {
            File file = new File(TOUCH_FOD_STATUS);
            if (status >= 0 && file.exists()) {
                fos = new FileOutputStream(file);
                fos.write(("" + status).getBytes());
                res = true;
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (fos != null) {
                fos.close();
            }
        } catch (Throwable th) {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
        return res;
    }

    private int getStatus4Touch(int cmd, int param) {
        switch (cmd) {
            case 1:
                return 1;
            case 2:
            case 6:
            case 8:
                return 0;
            case 3:
                if (param == 0) {
                    return 1;
                }
                return 0;
            case 4:
                if (getLockoutMode() == 0) {
                    return 0;
                }
                return 3;
            case 5:
                return 3;
            case 7:
                return 2;
            case 9:
                if (param == 0) {
                    return 0;
                }
                return 2;
            default:
                return -1;
        }
    }

    private String getCmdStr(int cmd) {
        switch (cmd) {
            case 1:
                return "CMD_APP_AUTHEN";
            case 2:
                return "CMD_APP_CANCEL_AUTHEN";
            case 3:
                return "CMD_VENDOR_AUTHENTICATED";
            case 4:
                return "CMD_VENDOR_ERROR";
            case 5:
                return "CMD_FW_LOCK_CANCEL";
            case 6:
                return "CMD_FW_TOP_APP_CANCEL";
            case 7:
                return "CMD_APP_ENROLL";
            case 8:
                return "CMD_APP_CANCEL_ENROLL";
            case 9:
                return "CMD_VENDOR_ENROLL_RES";
            default:
                return UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
        }
    }

    private boolean isNightMode() {
        return ((UiModeManager) this.mContext.getSystemService(UiModeManager.class)).getNightMode() == 2;
    }
}
