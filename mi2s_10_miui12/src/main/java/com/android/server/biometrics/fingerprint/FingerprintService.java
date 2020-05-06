package com.android.server.biometrics.fingerprint;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.UserInfo;
import android.hardware.biometrics.BiometricAuthenticator;
import android.hardware.biometrics.IBiometricServiceLockoutResetCallback;
import android.hardware.biometrics.IBiometricServiceReceiverInternal;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprintClientCallback;
import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.fingerprint.IFingerprintClientActiveCallback;
import android.hardware.fingerprint.IFingerprintService;
import android.hardware.fingerprint.IFingerprintServiceReceiver;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SELinux;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.util.DumpUtils;
import com.android.server.SystemServerInitThreadPool;
import com.android.server.am.AssistDataRequester;
import com.android.server.biometrics.BiometricServiceBase;
import com.android.server.biometrics.BiometricUtils;
import com.android.server.biometrics.ClientMonitor;
import com.android.server.biometrics.Constants;
import com.android.server.biometrics.EnumerateClient;
import com.android.server.biometrics.RemovalClient;
import com.android.server.biometrics.fingerprint.FingerprintService;
import com.android.server.utils.PriorityDump;
import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FingerprintService extends BiometricServiceBase {
    private static final String ACTION_LOCKOUT_RESET = "com.android.server.biometrics.fingerprint.ACTION_LOCKOUT_RESET";
    private static final boolean DEBUG = true;
    private static final long FAIL_LOCKOUT_TIMEOUT_MS = 30000;
    private static final String FP_DATA_DIR = "fpdata";
    private static final String KEY_LOCKOUT_RESET_USER = "lockout_reset_user";
    private static final int MAX_FAILED_ATTEMPTS_LOCKOUT_PERMANENT = 20;
    private static final int MAX_FAILED_ATTEMPTS_LOCKOUT_TIMED = 5;
    protected static final String TAG = "FingerprintService";
    private final AlarmManager mAlarmManager;
    /* access modifiers changed from: private */
    public final CopyOnWriteArrayList<IFingerprintClientActiveCallback> mClientActiveCallbacks = new CopyOnWriteArrayList<>();
    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public IBiometricsFingerprint mDaemon;
    private IBiometricsFingerprintClientCallback mDaemonCallback = new IBiometricsFingerprintClientCallback.Stub() {
        public void onEnrollResult(long deviceId, int fingerId, int groupId, int remaining) {
            FingerprintService.this.mHandler.post(new Runnable(groupId, fingerId, deviceId, remaining) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                }

                public final void run() {
                    FingerprintService.AnonymousClass1.this.lambda$onEnrollResult$0$FingerprintService$1(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$onEnrollResult$0$FingerprintService$1(int groupId, int fingerId, long deviceId, int remaining) {
            FingerprintService.super.handleEnrollResult(new Fingerprint(FingerprintService.this.getBiometricUtils().getUniqueName(FingerprintService.this.getContext(), groupId), groupId, fingerId, deviceId), remaining);
        }

        public void onAcquired(long deviceId, int acquiredInfo, int vendorCode) {
            FingerprintService.this.mHandler.post(new Runnable(deviceId, acquiredInfo, vendorCode) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final void run() {
                    FingerprintService.AnonymousClass1.this.lambda$onAcquired$1$FingerprintService$1(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$onAcquired$1$FingerprintService$1(long deviceId, int acquiredInfo, int vendorCode) {
            FingerprintService.super.handleAcquired(deviceId, acquiredInfo, vendorCode);
        }

        public void onAuthenticated(long deviceId, int fingerId, int groupId, ArrayList<Byte> token) {
            FingerprintService.this.mHandler.post(new Runnable(groupId, fingerId, deviceId, token) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ ArrayList f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                }

                public final void run() {
                    FingerprintService.AnonymousClass1.this.lambda$onAuthenticated$2$FingerprintService$1(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$onAuthenticated$2$FingerprintService$1(int groupId, int fingerId, long deviceId, ArrayList token) {
            FingerprintService.super.handleAuthenticated(new Fingerprint("", groupId, fingerId, deviceId), token);
        }

        public void onError(long deviceId, int error, int vendorCode) {
            FingerprintService.this.mHandler.post(new Runnable(deviceId, error, vendorCode) {
                private final /* synthetic */ long f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r4;
                    this.f$3 = r5;
                }

                public final void run() {
                    FingerprintService.AnonymousClass1.this.lambda$onError$3$FingerprintService$1(this.f$1, this.f$2, this.f$3);
                }
            });
        }

        public /* synthetic */ void lambda$onError$3$FingerprintService$1(long deviceId, int error, int vendorCode) {
            FingerprintService.super.handleError(deviceId, error, vendorCode);
            if (error == 1) {
                Slog.w(FingerprintService.TAG, "Got ERROR_HW_UNAVAILABLE; try reconnecting next client.");
                synchronized (this) {
                    IBiometricsFingerprint unused = FingerprintService.this.mDaemon = null;
                    long unused2 = FingerprintService.this.mHalDeviceId = 0;
                    int unused3 = FingerprintService.this.mCurrentUserId = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
                }
            }
        }

        public void onRemoved(long deviceId, int fingerId, int groupId, int remaining) {
            FingerprintService.this.mHandler.post(new Runnable(groupId, fingerId, deviceId, remaining) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                }

                public final void run() {
                    FingerprintService.AnonymousClass1.this.lambda$onRemoved$4$FingerprintService$1(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$onRemoved$4$FingerprintService$1(int groupId, int fingerId, long deviceId, int remaining) {
            ClientMonitor access$9600 = FingerprintService.this.getCurrentClient();
            FingerprintService.super.handleRemoved(new Fingerprint("", groupId, fingerId, deviceId), remaining);
        }

        public void onEnumerate(long deviceId, int fingerId, int groupId, int remaining) {
            FingerprintService.this.mHandler.post(new Runnable(groupId, fingerId, deviceId, remaining) {
                private final /* synthetic */ int f$1;
                private final /* synthetic */ int f$2;
                private final /* synthetic */ long f$3;
                private final /* synthetic */ int f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r6;
                }

                public final void run() {
                    FingerprintService.AnonymousClass1.this.lambda$onEnumerate$5$FingerprintService$1(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            });
        }

        public /* synthetic */ void lambda$onEnumerate$5$FingerprintService$1(int groupId, int fingerId, long deviceId, int remaining) {
            FingerprintService.super.handleEnumerate(new Fingerprint("", groupId, fingerId, deviceId), remaining);
        }
    };
    /* access modifiers changed from: private */
    public final BiometricServiceBase.DaemonWrapper mDaemonWrapper = new BiometricServiceBase.DaemonWrapper() {
        public int authenticate(long operationId, int groupId) throws RemoteException {
            IBiometricsFingerprint daemon = FingerprintService.this.getFingerprintDaemon();
            if (daemon != null) {
                return daemon.authenticate(operationId, groupId);
            }
            Slog.w(FingerprintService.TAG, "authenticate(): no fingerprint HAL!");
            return 3;
        }

        public int cancel() throws RemoteException {
            IBiometricsFingerprint daemon = FingerprintService.this.getFingerprintDaemon();
            if (daemon != null) {
                return daemon.cancel();
            }
            Slog.w(FingerprintService.TAG, "cancel(): no fingerprint HAL!");
            return 3;
        }

        public int remove(int groupId, int biometricId) throws RemoteException {
            IBiometricsFingerprint daemon = FingerprintService.this.getFingerprintDaemon();
            if (daemon != null) {
                return daemon.remove(groupId, biometricId);
            }
            Slog.w(FingerprintService.TAG, "remove(): no fingerprint HAL!");
            return 3;
        }

        public int enumerate() throws RemoteException {
            IBiometricsFingerprint daemon = FingerprintService.this.getFingerprintDaemon();
            if (daemon != null) {
                return daemon.enumerate();
            }
            Slog.w(FingerprintService.TAG, "enumerate(): no fingerprint HAL!");
            return 3;
        }

        public int enroll(byte[] cryptoToken, int groupId, int timeout, ArrayList<Integer> arrayList) throws RemoteException {
            IBiometricsFingerprint daemon = FingerprintService.this.getFingerprintDaemon();
            if (daemon != null) {
                return daemon.enroll(cryptoToken, groupId, timeout);
            }
            Slog.w(FingerprintService.TAG, "enroll(): no fingerprint HAL!");
            return 3;
        }

        public void resetLockout(byte[] token) throws RemoteException {
            Slog.e(FingerprintService.TAG, "Not supported");
        }
    };
    /* access modifiers changed from: private */
    public final SparseIntArray mFailedAttempts = new SparseIntArray();
    private final FingerprintConstants mFingerprintConstants = new FingerprintConstants();
    private final LockoutReceiver mLockoutReceiver = new LockoutReceiver();
    protected final ResetFailedAttemptsForUserRunnable mResetFailedAttemptsForCurrentUserRunnable = new ResetFailedAttemptsForUserRunnable();
    /* access modifiers changed from: private */
    public final SparseBooleanArray mTimedLockoutCleared = new SparseBooleanArray();

    private final class ResetFailedAttemptsForUserRunnable implements Runnable {
        private ResetFailedAttemptsForUserRunnable() {
        }

        public void run() {
            FingerprintService.this.resetFailedAttemptsForUser(true, ActivityManager.getCurrentUser());
        }
    }

    private final class LockoutReceiver extends BroadcastReceiver {
        private LockoutReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String tag = FingerprintService.this.getTag();
            Slog.v(tag, "Resetting lockout: " + intent.getAction());
            if (FingerprintService.this.getLockoutResetIntent().equals(intent.getAction())) {
                FingerprintService.this.resetFailedAttemptsForUser(false, intent.getIntExtra(FingerprintService.KEY_LOCKOUT_RESET_USER, 0));
            }
        }
    }

    private final class FingerprintAuthClient extends BiometricServiceBase.AuthenticationClientImpl {
        /* access modifiers changed from: protected */
        public boolean isFingerprint() {
            return true;
        }

        public FingerprintAuthClient(Context context, BiometricServiceBase.DaemonWrapper daemon, long halDeviceId, IBinder token, BiometricServiceBase.ServiceListener listener, int targetUserId, int groupId, long opId, boolean restricted, String owner, int cookie, boolean requireConfirmation) {
            super(context, daemon, halDeviceId, token, listener, targetUserId, groupId, opId, restricted, owner, cookie, requireConfirmation);
        }

        /* access modifiers changed from: protected */
        public int statsModality() {
            return FingerprintService.this.statsModality();
        }

        public void resetFailedAttempts() {
            FingerprintService.this.resetFailedAttemptsForUser(true, ActivityManager.getCurrentUser());
        }

        public boolean shouldFrameworkHandleLockout() {
            return true;
        }

        public boolean wasUserDetected() {
            return false;
        }

        public int handleFailedAttempt() {
            int currentUser = ActivityManager.getCurrentUser();
            FingerprintService.this.mFailedAttempts.put(currentUser, FingerprintService.this.mFailedAttempts.get(currentUser, 0) + 1);
            FingerprintService.this.mTimedLockoutCleared.put(ActivityManager.getCurrentUser(), false);
            if (FingerprintService.this.getLockoutMode() != 0) {
                FingerprintService.this.scheduleLockoutResetForUser(currentUser);
            }
            return super.handleFailedAttempt();
        }
    }

    private final class FingerprintServiceWrapper extends IFingerprintService.Stub {
        private FingerprintServiceWrapper() {
        }

        public long preEnroll(IBinder token) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            return FingerprintService.this.startPreEnroll(token);
        }

        public int postEnroll(IBinder token) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            return FingerprintService.this.startPostEnroll(token);
        }

        public void enroll(IBinder token, byte[] cryptoToken, int userId, IFingerprintServiceReceiver receiver, int flags, String opPackageName) {
            int currentUserId;
            int i = userId;
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            boolean restricted = FingerprintService.this.isRestricted();
            int currentUserId2 = userId;
            int unused = FingerprintService.this.mRequestId = 0;
            if (!FingerprintService.this.mConfigNeedFake || FingerprintService.this.mRootUserId == i) {
                currentUserId = currentUserId2;
            } else {
                int unused2 = FingerprintService.this.mRequestId = i;
                currentUserId = FingerprintService.this.mRootUserId;
            }
            Context context = FingerprintService.this.getContext();
            BiometricServiceBase.DaemonWrapper access$1500 = FingerprintService.this.mDaemonWrapper;
            long access$1600 = FingerprintService.this.mHalDeviceId;
            Context context2 = context;
            BiometricServiceBase.DaemonWrapper daemonWrapper = access$1500;
            long j = access$1600;
            IBinder iBinder = token;
            FingerprintService.this.enrollInternal(new BiometricServiceBase.EnrollClientImpl(this, context2, daemonWrapper, j, iBinder, new ServiceListenerImpl(receiver), currentUserId, currentUserId, cryptoToken, restricted, opPackageName, new int[0]) {
                final /* synthetic */ FingerprintServiceWrapper this$1;

                {
                    FingerprintServiceWrapper fingerprintServiceWrapper = this$1;
                    this.this$1 = fingerprintServiceWrapper;
                }

                public boolean shouldVibrate() {
                    return true;
                }

                /* access modifiers changed from: protected */
                public int statsModality() {
                    return FingerprintService.this.statsModality();
                }
            }, i);
        }

        public void cancelEnrollment(IBinder token) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            FingerprintService.this.cancelEnrollmentInternal(token);
        }

        public void authenticate(IBinder token, long opId, int groupId, IFingerprintServiceReceiver receiver, int flags, String opPackageName) {
            int currentUserId;
            int i = groupId;
            String str = opPackageName;
            FingerprintService.this.updateActiveGroup(i, str);
            boolean restricted = FingerprintService.this.isRestricted();
            int currentUserId2 = groupId;
            int unused = FingerprintService.this.mRequestId = 0;
            if (!FingerprintService.this.mConfigNeedFake || FingerprintService.this.mRootUserId == i) {
                currentUserId = currentUserId2;
            } else {
                int unused2 = FingerprintService.this.mRequestId = i;
                currentUserId = FingerprintService.this.mRootUserId;
            }
            FingerprintService fingerprintService = FingerprintService.this;
            BiometricServiceBase.AuthenticationClientImpl client = new FingerprintAuthClient(fingerprintService.getContext(), FingerprintService.this.mDaemonWrapper, FingerprintService.this.mHalDeviceId, token, new ServiceListenerImpl(receiver), FingerprintService.this.mCurrentUserId, currentUserId, opId, restricted, opPackageName, 0, false);
            client.mFlags = flags;
            FingerprintService.this.authenticateInternal(client, opId, str);
        }

        public void prepareForAuthentication(IBinder token, long opId, int groupId, IBiometricServiceReceiverInternal wrapperReceiver, String opPackageName, int cookie, int callingUid, int callingPid, int callingUserId) {
            int i = groupId;
            FingerprintService.this.checkPermission("android.permission.MANAGE_BIOMETRIC");
            FingerprintService.this.updateActiveGroup(i, opPackageName);
            int currentUserId = groupId;
            int unused = FingerprintService.this.mRequestId = 0;
            if (FingerprintService.this.mConfigNeedFake && FingerprintService.this.mRootUserId != i) {
                int unused2 = FingerprintService.this.mRequestId = i;
                currentUserId = FingerprintService.this.mRootUserId;
            }
            FingerprintService fingerprintService = FingerprintService.this;
            FingerprintService.this.authenticateInternal(new FingerprintAuthClient(fingerprintService.getContext(), FingerprintService.this.mDaemonWrapper, FingerprintService.this.mHalDeviceId, token, new BiometricPromptServiceListenerImpl(wrapperReceiver), FingerprintService.this.mCurrentUserId, currentUserId, opId, true, opPackageName, cookie, false), opId, opPackageName, callingUid, callingPid, callingUserId);
        }

        public void startPreparedClient(int cookie) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_BIOMETRIC");
            FingerprintService.this.startCurrentClient(cookie);
        }

        public void cancelAuthentication(IBinder token, String opPackageName) {
            FingerprintService.this.cancelAuthenticationInternal(token, opPackageName);
        }

        public void cancelAuthenticationFromService(IBinder token, String opPackageName, int callingUid, int callingPid, int callingUserId, boolean fromClient) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_BIOMETRIC");
            FingerprintService.this.cancelAuthenticationInternal(token, opPackageName, callingUid, callingPid, callingUserId, fromClient);
        }

        public void setActiveUser(int userId) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            FingerprintService.this.setActiveUserInternal(userId);
        }

        public void remove(IBinder token, int fingerId, int groupId, int userId, IFingerprintServiceReceiver receiver) {
            int currentUserId;
            int i = userId;
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            if (token == null) {
                Slog.w(FingerprintService.TAG, "remove(): token is null");
                return;
            }
            boolean restricted = FingerprintService.this.isRestricted();
            int currentUserId2 = userId;
            int unused = FingerprintService.this.mRequestId = 0;
            if (!FingerprintService.this.mConfigNeedFake || FingerprintService.this.mRootUserId == i) {
                currentUserId = currentUserId2;
            } else {
                int unused2 = FingerprintService.this.mRequestId = i;
                currentUserId = FingerprintService.this.mRootUserId;
            }
            FingerprintService.this.removeInternal(new RemovalClient(FingerprintService.this.getContext(), FingerprintService.this.getConstants(), FingerprintService.this.mDaemonWrapper, FingerprintService.this.mHalDeviceId, token, new ServiceListenerImpl(receiver), fingerId, groupId, currentUserId, restricted, token.toString(), FingerprintService.this.getBiometricUtils()) {
                /* access modifiers changed from: protected */
                public int statsModality() {
                    return FingerprintService.this.statsModality();
                }
            });
        }

        public void enumerate(IBinder token, int userId, IFingerprintServiceReceiver receiver) {
            int currentUserId;
            int i = userId;
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            boolean restricted = FingerprintService.this.isRestricted();
            int currentUserId2 = userId;
            int unused = FingerprintService.this.mRequestId = 0;
            if (!FingerprintService.this.mConfigNeedFake || FingerprintService.this.mRootUserId == i) {
                currentUserId = currentUserId2;
            } else {
                int unused2 = FingerprintService.this.mRequestId = i;
                currentUserId = FingerprintService.this.mRootUserId;
            }
            FingerprintService.this.enumerateInternal(new EnumerateClient(FingerprintService.this.getContext(), FingerprintService.this.getConstants(), FingerprintService.this.mDaemonWrapper, FingerprintService.this.mHalDeviceId, token, new ServiceListenerImpl(receiver), currentUserId, currentUserId, restricted, FingerprintService.this.getContext().getOpPackageName()) {
                /* access modifiers changed from: protected */
                public int statsModality() {
                    return FingerprintService.this.statsModality();
                }
            });
        }

        public void addLockoutResetCallback(IBiometricServiceLockoutResetCallback callback) throws RemoteException {
            FingerprintService.super.addLockoutResetCallback(callback);
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(FingerprintService.this.getContext(), FingerprintService.TAG, pw)) {
                long ident = Binder.clearCallingIdentity();
                try {
                    if (args.length <= 0 || !PriorityDump.PROTO_ARG.equals(args[0])) {
                        FingerprintService.this.dumpInternal(pw);
                    } else {
                        FingerprintService.this.dumpProto(fd);
                    }
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        public boolean isHardwareDetected(long deviceId, String opPackageName) {
            boolean z = false;
            if (!FingerprintService.this.canUseBiometric(opPackageName, false, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId())) {
                return false;
            }
            long token = Binder.clearCallingIdentity();
            try {
                if (!(FingerprintService.this.getFingerprintDaemon() == null || FingerprintService.this.mHalDeviceId == 0)) {
                    z = true;
                }
                return z;
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void rename(final int fingerId, int groupId, final String name) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            if (FingerprintService.this.isCurrentUserOrProfile(groupId)) {
                FingerprintService.this.mHandler.post(new Runnable() {
                    public void run() {
                        FingerprintService.this.getBiometricUtils().renameBiometricForUser(FingerprintService.this.getContext(), FingerprintService.this.mRootUserId, fingerId, name);
                    }
                });
            }
        }

        public List<Fingerprint> getEnrolledFingerprints(int userId, String opPackageName) {
            if (!FingerprintService.this.canUseBiometric(opPackageName, false, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId())) {
                return Collections.emptyList();
            }
            return FingerprintService.this.getEnrolledTemplates(userId);
        }

        public boolean hasEnrolledFingerprints(int userId, String opPackageName) {
            if (!FingerprintService.this.canUseBiometric(opPackageName, false, Binder.getCallingUid(), Binder.getCallingPid(), UserHandle.getCallingUserId())) {
                return false;
            }
            return FingerprintService.this.hasEnrolledBiometrics(userId);
        }

        public long getAuthenticatorId(String opPackageName) {
            return FingerprintService.super.getAuthenticatorId(opPackageName);
        }

        public void resetTimeout(byte[] token) {
            FingerprintService.this.checkPermission("android.permission.RESET_FINGERPRINT_LOCKOUT");
            FingerprintService fingerprintService = FingerprintService.this;
            if (!fingerprintService.hasEnrolledBiometrics(fingerprintService.mCurrentUserId)) {
                Slog.w(FingerprintService.TAG, "Ignoring lockout reset, no templates enrolled");
            } else {
                FingerprintService.this.mHandler.post(FingerprintService.this.mResetFailedAttemptsForCurrentUserRunnable);
            }
        }

        public boolean isClientActive() {
            boolean z;
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            synchronized (FingerprintService.this) {
                if (FingerprintService.this.getCurrentClient() == null) {
                    if (FingerprintService.this.getPendingClient() == null) {
                        z = false;
                    }
                }
                z = true;
            }
            return z;
        }

        public void addClientActiveCallback(IFingerprintClientActiveCallback callback) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            FingerprintService.this.mClientActiveCallbacks.add(callback);
        }

        public void removeClientActiveCallback(IFingerprintClientActiveCallback callback) {
            FingerprintService.this.checkPermission("android.permission.MANAGE_FINGERPRINT");
            FingerprintService.this.mClientActiveCallbacks.remove(callback);
        }

        public int extCmd(IBinder token, int groupId, int cmd, int param, String opPackageName) {
            Slog.d(FingerprintService.TAG, "extCmd() cmd: " + cmd + " param: " + param);
            return FingerprintService.this.startExtCmd(token, FingerprintService.this.getUserOrWorkProfileId(opPackageName, groupId), cmd, param);
        }
    }

    private class BiometricPromptServiceListenerImpl extends BiometricServiceBase.BiometricServiceListener {
        BiometricPromptServiceListenerImpl(IBiometricServiceReceiverInternal wrapperReceiver) {
            super(wrapperReceiver);
        }

        public void onAcquired(long deviceId, int acquiredInfo, int vendorCode) throws RemoteException {
            if (getWrapperReceiver() != null) {
                getWrapperReceiver().onAcquired(acquiredInfo, FingerprintManager.getAcquiredString(FingerprintService.this.getContext(), acquiredInfo, vendorCode));
            }
        }

        public void onError(long deviceId, int error, int vendorCode, int cookie) throws RemoteException {
            if (getWrapperReceiver() != null) {
                getWrapperReceiver().onError(cookie, error, FingerprintManager.getErrorString(FingerprintService.this.getContext(), error, vendorCode));
            }
        }
    }

    private class ServiceListenerImpl implements BiometricServiceBase.ServiceListener {
        private IFingerprintServiceReceiver mFingerprintServiceReceiver;

        public ServiceListenerImpl(IFingerprintServiceReceiver receiver) {
            this.mFingerprintServiceReceiver = receiver;
        }

        public void onEnrollResult(BiometricAuthenticator.Identifier identifier, int remaining) throws RemoteException {
            IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
            if (iFingerprintServiceReceiver != null) {
                Fingerprint fp = (Fingerprint) identifier;
                iFingerprintServiceReceiver.onEnrollResult(fp.getDeviceId(), fp.getBiometricId(), fp.getGroupId(), remaining);
            }
        }

        public void onAcquired(long deviceId, int acquiredInfo, int vendorCode) throws RemoteException {
            IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
            if (iFingerprintServiceReceiver != null) {
                iFingerprintServiceReceiver.onAcquired(deviceId, acquiredInfo, vendorCode);
            }
        }

        public void onAuthenticationSucceeded(long deviceId, BiometricAuthenticator.Identifier biometric, int userId) throws RemoteException {
            if (this.mFingerprintServiceReceiver == null) {
                return;
            }
            if (biometric == null || (biometric instanceof Fingerprint)) {
                this.mFingerprintServiceReceiver.onAuthenticationSucceeded(deviceId, (Fingerprint) biometric, userId);
            } else {
                Slog.e(FingerprintService.TAG, "onAuthenticationSucceeded received non-fingerprint biometric");
            }
        }

        public void onAuthenticationFailed(long deviceId) throws RemoteException {
            IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
            if (iFingerprintServiceReceiver != null) {
                iFingerprintServiceReceiver.onAuthenticationFailed(deviceId);
            }
        }

        public void onError(long deviceId, int error, int vendorCode, int cookie) throws RemoteException {
            IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
            if (iFingerprintServiceReceiver != null) {
                iFingerprintServiceReceiver.onError(deviceId, error, vendorCode);
            }
        }

        public void onRemoved(BiometricAuthenticator.Identifier identifier, int remaining) throws RemoteException {
            IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
            if (iFingerprintServiceReceiver != null) {
                Fingerprint fp = (Fingerprint) identifier;
                iFingerprintServiceReceiver.onRemoved(fp.getDeviceId(), fp.getBiometricId(), fp.getGroupId(), remaining);
            }
        }

        public void onEnumerated(BiometricAuthenticator.Identifier identifier, int remaining) throws RemoteException {
            IFingerprintServiceReceiver iFingerprintServiceReceiver = this.mFingerprintServiceReceiver;
            if (iFingerprintServiceReceiver != null) {
                Fingerprint fp = (Fingerprint) identifier;
                iFingerprintServiceReceiver.onEnumerated(fp.getDeviceId(), fp.getBiometricId(), fp.getGroupId(), remaining);
            }
        }
    }

    public FingerprintService(Context context) {
        super(context);
        this.mAlarmManager = (AlarmManager) context.getSystemService(AlarmManager.class);
        context.registerReceiver(this.mLockoutReceiver, new IntentFilter(getLockoutResetIntent()), getLockoutBroadcastPermission(), (Handler) null);
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.biometrics.fingerprint.FingerprintService$FingerprintServiceWrapper, android.os.IBinder] */
    public void onStart() {
        super.onStart();
        publishBinderService("fingerprint", new FingerprintServiceWrapper());
        SystemServerInitThreadPool.get().submit(new Runnable() {
            public final void run() {
                IBiometricsFingerprint unused = FingerprintService.this.getFingerprintDaemon();
            }
        }, "FingerprintService.onStart");
    }

    /* access modifiers changed from: protected */
    public String getTag() {
        return TAG;
    }

    /* access modifiers changed from: protected */
    public BiometricServiceBase.DaemonWrapper getDaemonWrapper() {
        return this.mDaemonWrapper;
    }

    /* access modifiers changed from: protected */
    public BiometricUtils getBiometricUtils() {
        return FingerprintUtils.getInstance();
    }

    /* access modifiers changed from: protected */
    public Constants getConstants() {
        return this.mFingerprintConstants;
    }

    /* access modifiers changed from: protected */
    public boolean hasReachedEnrollmentLimit(int userId) {
        if (getEnrolledTemplates(userId).size() < getContext().getResources().getInteger(17694811)) {
            return false;
        }
        Slog.w(TAG, "Too many fingerprints registered");
        return true;
    }

    public void serviceDied(long cookie) {
        super.serviceDied(cookie);
        this.mDaemon = null;
    }

    /* access modifiers changed from: protected */
    public void updateActiveGroup(int userId, String clientPackage) {
        if (this.mConfigNeedFake) {
            userId = this.mRootUserId;
        }
        IBiometricsFingerprint daemon = getFingerprintDaemon();
        if (daemon != null) {
            try {
                int userId2 = getUserOrWorkProfileId(clientPackage, userId);
                if (userId2 != this.mCurrentUserId) {
                    int firstSdkInt = Build.VERSION.FIRST_SDK_INT;
                    if (firstSdkInt < 1) {
                        Slog.e(TAG, "First SDK version " + firstSdkInt + " is invalid; must be at least VERSION_CODES.BASE");
                    }
                    File fpDir = new File(Environment.getDataVendorDeDirectory(userId2), FP_DATA_DIR);
                    if (!fpDir.exists()) {
                        if (!fpDir.mkdir()) {
                            Slog.v(TAG, "Cannot make directory: " + fpDir.getAbsolutePath());
                            return;
                        } else if (!SELinux.restorecon(fpDir)) {
                            Slog.w(TAG, "Restorecons failed. Directory will have wrong label.");
                            return;
                        }
                    }
                    daemon.setActiveGroup(userId2, fpDir.getAbsolutePath());
                    this.mCurrentUserId = userId2;
                }
                this.mAuthenticatorIds.put(Integer.valueOf(userId2), Long.valueOf(hasEnrolledBiometrics(userId2) ? daemon.getAuthenticatorId() : 0));
            } catch (RemoteException e) {
                Slog.e(TAG, "Failed to setActiveGroup():", e);
            }
        }
    }

    /* access modifiers changed from: protected */
    public String getLockoutResetIntent() {
        return ACTION_LOCKOUT_RESET;
    }

    /* access modifiers changed from: protected */
    public String getLockoutBroadcastPermission() {
        return "android.permission.RESET_FINGERPRINT_LOCKOUT";
    }

    /* access modifiers changed from: protected */
    public long getHalDeviceId() {
        return this.mHalDeviceId;
    }

    /* access modifiers changed from: protected */
    public boolean hasEnrolledBiometrics(int userId) {
        int userId2 = this.mRootUserId;
        if (!this.mConfigNeedFake && userId2 != UserHandle.getCallingUserId()) {
            checkPermission("android.permission.INTERACT_ACROSS_USERS");
        }
        return getBiometricUtils().getBiometricsForUser(getContext(), userId2).size() > 0;
    }

    /* access modifiers changed from: protected */
    public String getManageBiometricPermission() {
        return "android.permission.MANAGE_FINGERPRINT";
    }

    /* access modifiers changed from: protected */
    public void checkUseBiometricPermission() {
        if (getContext().checkCallingPermission("android.permission.USE_FINGERPRINT") != 0) {
            checkPermission("android.permission.USE_BIOMETRIC");
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkAppOps(int uid, String opPackageName) {
        if (this.mAppOps.noteOp(78, uid, opPackageName) == 0 || this.mAppOps.noteOp(55, uid, opPackageName) == 0) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public List<Fingerprint> getEnrolledTemplates(int userId) {
        if (this.mConfigNeedFake && this.mRootUserId != userId) {
            this.mRequestId = userId;
            userId = this.mRootUserId;
        }
        if (userId != UserHandle.getCallingUserId()) {
            checkPermission("android.permission.INTERACT_ACROSS_USERS");
        }
        return getBiometricUtils().getBiometricsForUser(getContext(), userId);
    }

    /* access modifiers changed from: protected */
    public void notifyClientActiveCallbacks(boolean isActive) {
        List<IFingerprintClientActiveCallback> callbacks = this.mClientActiveCallbacks;
        for (int i = 0; i < callbacks.size(); i++) {
            try {
                callbacks.get(i).onClientActiveChanged(isActive);
            } catch (RemoteException e) {
                this.mClientActiveCallbacks.remove(callbacks.get(i));
            }
        }
    }

    /* access modifiers changed from: protected */
    public int statsModality() {
        return 1;
    }

    /* access modifiers changed from: protected */
    public int getLockoutMode() {
        int currentUser = ActivityManager.getCurrentUser();
        int failedAttempts = this.mFailedAttempts.get(currentUser, 0);
        if (failedAttempts >= 20) {
            return 2;
        }
        if (failedAttempts <= 0 || this.mTimedLockoutCleared.get(currentUser, false) || failedAttempts % 5 != 0) {
            return 0;
        }
        return 1;
    }

    /* access modifiers changed from: private */
    public synchronized IBiometricsFingerprint getFingerprintDaemon() {
        if (this.mDaemon == null) {
            Slog.v(TAG, "mDaemon was null, reconnect to fingerprint");
            try {
                this.mDaemon = IBiometricsFingerprint.getService();
            } catch (NoSuchElementException e) {
            } catch (RemoteException e2) {
                Slog.e(TAG, "Failed to get biometric interface", e2);
            }
            if (this.mDaemon == null) {
                Slog.w(TAG, "fingerprint HIDL not available");
                return null;
            }
            this.mDaemon.asBinder().linkToDeath(this, 0);
            try {
                this.mHalDeviceId = this.mDaemon.setNotify(this.mDaemonCallback);
            } catch (RemoteException e3) {
                Slog.e(TAG, "Failed to open fingerprint HAL", e3);
                this.mDaemon = null;
            }
            Slog.v(TAG, "Fingerprint HAL id: " + this.mHalDeviceId);
            if (this.mHalDeviceId != 0) {
                loadAuthenticatorIds();
                updateActiveGroup(ActivityManager.getCurrentUser(), (String) null);
                doTemplateCleanupForUser(ActivityManager.getCurrentUser());
            } else {
                Slog.w(TAG, "Failed to open Fingerprint HAL!");
                MetricsLogger.count(getContext(), "fingerprintd_openhal_error", 1);
                this.mDaemon = null;
            }
        }
        return this.mDaemon;
    }

    /* access modifiers changed from: private */
    public long startPreEnroll(IBinder token) {
        IBiometricsFingerprint daemon = getFingerprintDaemon();
        if (daemon == null) {
            Slog.w(TAG, "startPreEnroll: no fingerprint HAL!");
            return 0;
        }
        try {
            return daemon.preEnroll();
        } catch (RemoteException e) {
            Slog.e(TAG, "startPreEnroll failed", e);
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public int startPostEnroll(IBinder token) {
        IBiometricsFingerprint daemon = getFingerprintDaemon();
        if (daemon == null) {
            Slog.w(TAG, "startPostEnroll: no fingerprint HAL!");
            return 0;
        }
        try {
            return daemon.postEnroll();
        } catch (RemoteException e) {
            Slog.e(TAG, "startPostEnroll failed", e);
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public void resetFailedAttemptsForUser(boolean clearAttemptCounter, int userId) {
        if (getLockoutMode() != 0) {
            String tag = getTag();
            Slog.v(tag, "Reset biometric lockout, clearAttemptCounter=" + clearAttemptCounter);
        }
        if (clearAttemptCounter) {
            this.mFailedAttempts.put(userId, 0);
        }
        this.mTimedLockoutCleared.put(userId, true);
        cancelLockoutResetForUser(userId);
        notifyLockoutResetMonitors();
    }

    private void cancelLockoutResetForUser(int userId) {
        this.mAlarmManager.cancel(getLockoutResetIntentForUser(userId));
    }

    /* access modifiers changed from: private */
    public void scheduleLockoutResetForUser(int userId) {
        this.mAlarmManager.setExact(2, SystemClock.elapsedRealtime() + 30000, getLockoutResetIntentForUser(userId));
    }

    private PendingIntent getLockoutResetIntentForUser(int userId) {
        return PendingIntent.getBroadcast(getContext(), userId, new Intent(getLockoutResetIntent()).putExtra(KEY_LOCKOUT_RESET_USER, userId), 134217728);
    }

    /* access modifiers changed from: private */
    public void dumpInternal(PrintWriter pw) {
        JSONObject dump = new JSONObject();
        try {
            dump.put("service", "Fingerprint Manager");
            JSONArray sets = new JSONArray();
            for (UserInfo user : UserManager.get(getContext()).getUsers()) {
                int userId = user.getUserHandle().getIdentifier();
                int N = getBiometricUtils().getBiometricsForUser(getContext(), userId).size();
                BiometricServiceBase.PerformanceStats stats = (BiometricServiceBase.PerformanceStats) this.mPerformanceMap.get(Integer.valueOf(userId));
                BiometricServiceBase.PerformanceStats cryptoStats = (BiometricServiceBase.PerformanceStats) this.mCryptoPerformanceMap.get(Integer.valueOf(userId));
                JSONObject set = new JSONObject();
                set.put("id", userId);
                set.put(AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, N);
                set.put("mRequestId", this.mRequestId);
                set.put("accept", stats != null ? stats.accept : 0);
                set.put("reject", stats != null ? stats.reject : 0);
                set.put("acquire", stats != null ? stats.acquire : 0);
                set.put("lockout", stats != null ? stats.lockout : 0);
                set.put("permanentLockout", stats != null ? stats.permanentLockout : 0);
                set.put("acceptCrypto", cryptoStats != null ? cryptoStats.accept : 0);
                set.put("rejectCrypto", cryptoStats != null ? cryptoStats.reject : 0);
                set.put("acquireCrypto", cryptoStats != null ? cryptoStats.acquire : 0);
                set.put("lockoutCrypto", cryptoStats != null ? cryptoStats.lockout : 0);
                set.put("permanentLockoutCrypto", cryptoStats != null ? cryptoStats.permanentLockout : 0);
                sets.put(set);
            }
            dump.put("prints", sets);
        } catch (JSONException e) {
            Slog.e(TAG, "dump formatting failure", e);
        }
        pw.println(dump);
        pw.println("HAL Deaths: " + this.mHALDeathCount);
        this.mHALDeathCount = 0;
    }

    /* access modifiers changed from: private */
    public void dumpProto(FileDescriptor fd) {
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        for (UserInfo user : UserManager.get(getContext()).getUsers()) {
            int userId = user.getUserHandle().getIdentifier();
            long userToken = proto.start(2246267895809L);
            proto.write(1120986464257L, userId);
            proto.write(1120986464258L, getBiometricUtils().getBiometricsForUser(getContext(), userId).size());
            BiometricServiceBase.PerformanceStats normal = (BiometricServiceBase.PerformanceStats) this.mPerformanceMap.get(Integer.valueOf(userId));
            if (normal != null) {
                long countsToken = proto.start(1146756268035L);
                proto.write(1120986464257L, normal.accept);
                proto.write(1120986464258L, normal.reject);
                proto.write(1120986464259L, normal.acquire);
                proto.write(1120986464260L, normal.lockout);
                proto.write(1120986464261L, normal.permanentLockout);
                proto.end(countsToken);
            }
            BiometricServiceBase.PerformanceStats crypto = (BiometricServiceBase.PerformanceStats) this.mCryptoPerformanceMap.get(Integer.valueOf(userId));
            if (crypto != null) {
                long countsToken2 = proto.start(1146756268036L);
                proto.write(1120986464257L, crypto.accept);
                proto.write(1120986464258L, crypto.reject);
                proto.write(1120986464259L, crypto.acquire);
                proto.write(1120986464260L, crypto.lockout);
                proto.write(1120986464261L, crypto.permanentLockout);
                proto.end(countsToken2);
            }
            proto.end(userToken);
        }
        proto.flush();
        this.mPerformanceMap.clear();
        this.mCryptoPerformanceMap.clear();
    }
}
