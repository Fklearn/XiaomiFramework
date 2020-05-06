package com.android.server.biometrics;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import com.android.server.biometrics.BiometricServiceBase;

public abstract class AuthenticationClient extends ClientMonitor {
    public static final int LOCKOUT_NONE = 0;
    public static final int LOCKOUT_PERMANENT = 2;
    public static final int LOCKOUT_TIMED = 1;
    public int mFlags = 0;
    private long mOpId;
    private final boolean mRequireConfirmation;
    private boolean mStarted;

    public abstract int handleFailedAttempt();

    public abstract void onStart();

    public abstract void onStop();

    public abstract boolean shouldFrameworkHandleLockout();

    public abstract boolean wasUserDetected();

    public void resetFailedAttempts() {
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AuthenticationClient(Context context, Constants constants, BiometricServiceBase.DaemonWrapper daemon, long halDeviceId, IBinder token, BiometricServiceBase.ServiceListener listener, int targetUserId, int groupId, long opId, boolean restricted, String owner, int cookie, boolean requireConfirmation) {
        super(context, constants, daemon, halDeviceId, token, listener, targetUserId, groupId, restricted, owner, cookie);
        this.mOpId = opId;
        this.mRequireConfirmation = requireConfirmation;
    }

    public void binderDied() {
        super.binderDied();
        stop(false);
    }

    /* access modifiers changed from: protected */
    public int statsAction() {
        return 2;
    }

    public boolean isBiometricPrompt() {
        return getCookie() != 0;
    }

    public boolean getRequireConfirmation() {
        return this.mRequireConfirmation;
    }

    /* access modifiers changed from: protected */
    public boolean isCryptoOperation() {
        return this.mOpId != 0;
    }

    public boolean onError(long deviceId, int error, int vendorCode) {
        if (!shouldFrameworkHandleLockout() && (error == 3 ? wasUserDetected() || isBiometricPrompt() : error == 7 || error == 9) && this.mStarted) {
            vibrateError();
        }
        return super.onError(deviceId, error, vendorCode);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v5, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v6, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v7, resolved type: int} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v8, resolved type: int} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onAuthenticated(android.hardware.biometrics.BiometricAuthenticator.Identifier r10, boolean r11, java.util.ArrayList<java.lang.Byte> r12) {
        /*
            r9 = this;
            java.lang.String r0 = "Remote exception"
            android.content.Context r2 = r9.getContext()
            boolean r4 = r9.mRequireConfirmation
            int r5 = r9.getTargetUserId()
            boolean r6 = r9.isBiometricPrompt()
            r1 = r9
            r3 = r11
            super.logOnAuthenticated(r2, r3, r4, r5, r6)
            java.lang.String r1 = r9.getOwnerString()
            com.android.server.fingerprint.FingerprintServiceInjector.reportFingerEvent(r1, r11)
            com.android.server.biometrics.BiometricServiceBase$ServiceListener r1 = r9.getListener()
            com.android.internal.logging.MetricsLogger r2 = r9.mMetricsLogger
            com.android.server.biometrics.Constants r3 = r9.mConstants
            int r3 = r3.actionBiometricAuth()
            r2.action(r3, r11)
            r2 = 0
            java.lang.String r3 = r9.getLogTag()     // Catch:{ RemoteException -> 0x016e }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x016e }
            r4.<init>()     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = "onAuthenticated("
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            r4.append(r11)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = "), ID:"
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            int r5 = r10.getBiometricId()     // Catch:{ RemoteException -> 0x016e }
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = ", Owner: "
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = r9.getOwnerString()     // Catch:{ RemoteException -> 0x016e }
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = ", isBP: "
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            boolean r5 = r9.isBiometricPrompt()     // Catch:{ RemoteException -> 0x016e }
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = ", listener: "
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            r4.append(r1)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = ", requireConfirmation: "
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            boolean r5 = r9.mRequireConfirmation     // Catch:{ RemoteException -> 0x016e }
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = ", user: "
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            int r5 = r9.getTargetUserId()     // Catch:{ RemoteException -> 0x016e }
            r4.append(r5)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r4 = r4.toString()     // Catch:{ RemoteException -> 0x016e }
            android.util.Slog.v(r3, r4)     // Catch:{ RemoteException -> 0x016e }
            r3 = 0
            r4 = 1
            if (r11 == 0) goto L_0x0109
            r9.mAlreadyDone = r4     // Catch:{ RemoteException -> 0x016e }
            r2 = 1
            boolean r4 = r9.shouldFrameworkHandleLockout()     // Catch:{ RemoteException -> 0x016e }
            if (r4 == 0) goto L_0x0097
            r9.resetFailedAttempts()     // Catch:{ RemoteException -> 0x016e }
        L_0x0097:
            r9.onStop()     // Catch:{ RemoteException -> 0x016e }
            int r4 = r12.size()     // Catch:{ RemoteException -> 0x016e }
            byte[] r4 = new byte[r4]     // Catch:{ RemoteException -> 0x016e }
        L_0x00a1:
            int r5 = r12.size()     // Catch:{ RemoteException -> 0x016e }
            if (r3 >= r5) goto L_0x00b6
            java.lang.Object r5 = r12.get(r3)     // Catch:{ RemoteException -> 0x016e }
            java.lang.Byte r5 = (java.lang.Byte) r5     // Catch:{ RemoteException -> 0x016e }
            byte r5 = r5.byteValue()     // Catch:{ RemoteException -> 0x016e }
            r4[r3] = r5     // Catch:{ RemoteException -> 0x016e }
            int r3 = r3 + 1
            goto L_0x00a1
        L_0x00b6:
            boolean r3 = r9.isBiometricPrompt()     // Catch:{ RemoteException -> 0x016e }
            if (r3 == 0) goto L_0x00c4
            if (r1 == 0) goto L_0x00c4
            boolean r3 = r9.mRequireConfirmation     // Catch:{ RemoteException -> 0x016e }
            r1.onAuthenticationSucceededInternal(r3, r4)     // Catch:{ RemoteException -> 0x016e }
            goto L_0x0108
        L_0x00c4:
            boolean r3 = r9.isBiometricPrompt()     // Catch:{ RemoteException -> 0x016e }
            if (r3 != 0) goto L_0x00fe
            if (r1 == 0) goto L_0x00fe
            android.security.KeyStore r3 = android.security.KeyStore.getInstance()     // Catch:{ RemoteException -> 0x016e }
            r3.addAuthToken(r4)     // Catch:{ RemoteException -> 0x016e }
            boolean r3 = r9.getIsRestricted()     // Catch:{ RemoteException -> 0x00f4 }
            if (r3 != 0) goto L_0x00e6
            long r5 = r9.getHalDeviceId()     // Catch:{ RemoteException -> 0x00f4 }
            int r3 = r9.getTargetUserId()     // Catch:{ RemoteException -> 0x00f4 }
            r1.onAuthenticationSucceeded(r5, r10, r3)     // Catch:{ RemoteException -> 0x00f4 }
            goto L_0x00f3
        L_0x00e6:
            long r5 = r9.getHalDeviceId()     // Catch:{ RemoteException -> 0x00f4 }
            r3 = 0
            int r7 = r9.getTargetUserId()     // Catch:{ RemoteException -> 0x00f4 }
            r1.onAuthenticationSucceeded(r5, r3, r7)     // Catch:{ RemoteException -> 0x00f4 }
        L_0x00f3:
            goto L_0x0108
        L_0x00f4:
            r3 = move-exception
            java.lang.String r5 = r9.getLogTag()     // Catch:{ RemoteException -> 0x016e }
            android.util.Slog.e(r5, r0, r3)     // Catch:{ RemoteException -> 0x016e }
            goto L_0x0108
        L_0x00fe:
            java.lang.String r3 = r9.getLogTag()     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r5 = "Client not listening"
            android.util.Slog.w(r3, r5)     // Catch:{ RemoteException -> 0x016e }
            r2 = 1
        L_0x0108:
            goto L_0x016d
        L_0x0109:
            if (r1 == 0) goto L_0x0112
            boolean r5 = com.android.server.biometrics.BiometricServiceBase.IS_FOD     // Catch:{ RemoteException -> 0x016e }
            if (r5 != 0) goto L_0x0112
            r9.vibrateError()     // Catch:{ RemoteException -> 0x016e }
        L_0x0112:
            int r5 = r9.handleFailedAttempt()     // Catch:{ RemoteException -> 0x016e }
            if (r5 == 0) goto L_0x014e
            boolean r6 = r9.shouldFrameworkHandleLockout()     // Catch:{ RemoteException -> 0x016e }
            if (r6 == 0) goto L_0x014e
            java.lang.String r6 = r9.getLogTag()     // Catch:{ RemoteException -> 0x016e }
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x016e }
            r7.<init>()     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r8 = "Forcing lockout (driver code should do this!), mode("
            r7.append(r8)     // Catch:{ RemoteException -> 0x016e }
            r7.append(r5)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r8 = ")"
            r7.append(r8)     // Catch:{ RemoteException -> 0x016e }
            java.lang.String r7 = r7.toString()     // Catch:{ RemoteException -> 0x016e }
            android.util.Slog.w(r6, r7)     // Catch:{ RemoteException -> 0x016e }
            r9.stop(r3)     // Catch:{ RemoteException -> 0x016e }
            if (r5 != r4) goto L_0x0142
            r6 = 7
            goto L_0x0144
        L_0x0142:
            r6 = 9
        L_0x0144:
            long r7 = r9.getHalDeviceId()     // Catch:{ RemoteException -> 0x016e }
            r9.onError(r7, r6, r3)     // Catch:{ RemoteException -> 0x016e }
            goto L_0x0169
        L_0x014e:
            if (r1 == 0) goto L_0x0169
            boolean r6 = r9.isBiometricPrompt()     // Catch:{ RemoteException -> 0x016e }
            if (r6 == 0) goto L_0x0162
            int r6 = r9.getCookie()     // Catch:{ RemoteException -> 0x016e }
            boolean r7 = r9.getRequireConfirmation()     // Catch:{ RemoteException -> 0x016e }
            r1.onAuthenticationFailedInternal(r6, r7)     // Catch:{ RemoteException -> 0x016e }
            goto L_0x0169
        L_0x0162:
            long r6 = r9.getHalDeviceId()     // Catch:{ RemoteException -> 0x016e }
            r1.onAuthenticationFailed(r6)     // Catch:{ RemoteException -> 0x016e }
        L_0x0169:
            if (r5 == 0) goto L_0x016c
            r3 = r4
        L_0x016c:
            r2 = r3
        L_0x016d:
            goto L_0x0177
        L_0x016e:
            r3 = move-exception
            java.lang.String r4 = r9.getLogTag()
            android.util.Slog.e(r4, r0, r3)
            r2 = 1
        L_0x0177:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.biometrics.AuthenticationClient.onAuthenticated(android.hardware.biometrics.BiometricAuthenticator$Identifier, boolean, java.util.ArrayList):boolean");
    }

    public int start() {
        this.mStarted = true;
        onStart();
        try {
            int result = getDaemonWrapper().authenticate(this.mOpId, getGroupId());
            if (result != 0) {
                String logTag = getLogTag();
                Slog.w(logTag, "startAuthentication failed, result=" + result);
                this.mMetricsLogger.histogram(this.mConstants.tagAuthStartError(), result);
                onError(getHalDeviceId(), 1, 0);
                return result;
            }
            String logTag2 = getLogTag();
            Slog.w(logTag2, "client " + getOwnerString() + " is authenticating...");
            return 0;
        } catch (RemoteException e) {
            Slog.e(getLogTag(), "startAuthentication failed", e);
            return 3;
        }
    }

    public int stop(boolean initiatedByClient) {
        if (this.mAlreadyCancelled) {
            Slog.w(getLogTag(), "stopAuthentication: already cancelled!");
            return 0;
        }
        this.mStarted = false;
        onStop();
        try {
            int result = getDaemonWrapper().cancel();
            if (result != 0) {
                String logTag = getLogTag();
                Slog.w(logTag, "stopAuthentication failed, result=" + result);
                return result;
            }
            String logTag2 = getLogTag();
            Slog.w(logTag2, "client " + getOwnerString() + " is no longer authenticating");
            this.mAlreadyCancelled = true;
            return 0;
        } catch (RemoteException e) {
            Slog.e(getLogTag(), "stopAuthentication failed", e);
            return 3;
        }
    }

    public boolean onEnrollResult(BiometricAuthenticator.Identifier identifier, int remaining) {
        Slog.w(getLogTag(), "onEnrollResult() called for authenticate!");
        return true;
    }

    public boolean onRemoved(BiometricAuthenticator.Identifier identifier, int remaining) {
        Slog.w(getLogTag(), "onRemoved() called for authenticate!");
        return true;
    }

    public boolean onEnumerationResult(BiometricAuthenticator.Identifier identifier, int remaining) {
        Slog.w(getLogTag(), "onEnumerationResult() called for authenticate!");
        return true;
    }
}
