package com.android.server.biometrics.fingerprint;

import com.android.server.biometrics.Constants;

public class FingerprintConstants implements Constants {
    public String logTag() {
        return "FingerprintService";
    }

    public String tagHalDied() {
        return "fingerprintd_died";
    }

    public String tagAuthToken() {
        return "fingerprint_token";
    }

    public String tagAuthStartError() {
        return "fingerprintd_auth_start_error";
    }

    public String tagEnrollStartError() {
        return "fingerprintd_enroll_start_error";
    }

    public String tagEnumerateStartError() {
        return "fingerprintd_enum_start_error";
    }

    public String tagRemoveStartError() {
        return "fingerprintd_remove_start_error";
    }

    public int actionBiometricAuth() {
        return 252;
    }

    public int actionBiometricEnroll() {
        return 251;
    }

    public int acquireVendorCode() {
        return 6;
    }
}
