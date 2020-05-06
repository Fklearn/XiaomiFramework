package com.miui.applicationlock.c;

import android.hardware.fingerprint.Fingerprint;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.util.Log;
import b.b.o.g.e;

class D extends FingerprintManager.AuthenticationCallback {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ E f3276a;

    D(E e) {
        this.f3276a = e;
    }

    public void onAuthenticationError(int i, CharSequence charSequence) {
        if (Build.VERSION.SDK_INT > 28 && 7 == i) {
            this.f3276a.e.a();
        }
    }

    public void onAuthenticationFailed() {
        this.f3276a.e.a();
    }

    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult authenticationResult) {
        super.onAuthenticationSucceeded(authenticationResult);
        if (authenticationResult != null) {
            try {
                Fingerprint fingerprint = (Fingerprint) e.a((Object) authenticationResult, Fingerprint.class, "getFingerprint", (Class<?>[]) null, new Object[0]);
                if (fingerprint != null) {
                    this.f3276a.e.a(this.f3276a.a(fingerprint));
                }
            } catch (Exception e) {
                Log.e("FingerprintHelperImpl", "onAuthenticationSucceeded exception: ", e);
            }
        }
    }
}
