package com.android.server.biometrics.iris;

import android.content.Context;
import android.hardware.biometrics.BiometricAuthenticator;
import com.android.server.biometrics.BiometricServiceBase;
import com.android.server.biometrics.BiometricUtils;
import com.android.server.biometrics.Constants;
import java.util.List;

public class IrisService extends BiometricServiceBase {
    private static final String TAG = "IrisService";

    public IrisService(Context context) {
        super(context);
    }

    public void onStart() {
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public String getTag() {
        return TAG;
    }

    /* access modifiers changed from: protected */
    public BiometricServiceBase.DaemonWrapper getDaemonWrapper() {
        return null;
    }

    /* access modifiers changed from: protected */
    public BiometricUtils getBiometricUtils() {
        return null;
    }

    /* access modifiers changed from: protected */
    public Constants getConstants() {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean hasReachedEnrollmentLimit(int userId) {
        return false;
    }

    /* access modifiers changed from: protected */
    public void updateActiveGroup(int userId, String clientPackage) {
    }

    /* access modifiers changed from: protected */
    public String getLockoutResetIntent() {
        return null;
    }

    /* access modifiers changed from: protected */
    public String getLockoutBroadcastPermission() {
        return null;
    }

    /* access modifiers changed from: protected */
    public long getHalDeviceId() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public boolean hasEnrolledBiometrics(int userId) {
        return false;
    }

    /* access modifiers changed from: protected */
    public String getManageBiometricPermission() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void checkUseBiometricPermission() {
    }

    /* access modifiers changed from: protected */
    public boolean checkAppOps(int uid, String opPackageName) {
        return false;
    }

    /* access modifiers changed from: protected */
    public List<? extends BiometricAuthenticator.Identifier> getEnrolledTemplates(int userId) {
        return null;
    }

    /* access modifiers changed from: protected */
    public int statsModality() {
        return 2;
    }

    /* access modifiers changed from: protected */
    public int getLockoutMode() {
        return 0;
    }
}
