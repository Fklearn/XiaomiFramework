package com.android.server.biometrics.face;

import com.android.server.biometrics.Constants;

public class FaceConstants implements Constants {
    public String logTag() {
        return "FaceService";
    }

    public String tagHalDied() {
        return "faced_died";
    }

    public String tagAuthToken() {
        return "face_token";
    }

    public String tagAuthStartError() {
        return "faced_auth_start_error";
    }

    public String tagEnrollStartError() {
        return "faced_enroll_start_error";
    }

    public String tagEnumerateStartError() {
        return "faced_enum_start_error";
    }

    public String tagRemoveStartError() {
        return "faced_remove_start_error";
    }

    public int actionBiometricAuth() {
        return 1504;
    }

    public int actionBiometricEnroll() {
        return 1505;
    }

    public int acquireVendorCode() {
        return 22;
    }
}
