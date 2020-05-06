package com.android.server.policy;

import android.os.PowerManager;
import android.os.SystemClock;
import com.android.server.policy.keyguard.KeyguardServiceDelegate;

public class MiuiKeyguardServiceDelegate extends AbstractKeyguardServiceDelegate {
    protected KeyguardServiceDelegate mKeyguardDelegate;
    protected PhoneWindowManager mPhoneWindowManager;
    protected PowerManager mPowerManager;

    public MiuiKeyguardServiceDelegate(PhoneWindowManager phoneWindowManager, KeyguardServiceDelegate keyguardDelegate, PowerManager powerManager) {
        this.mPhoneWindowManager = phoneWindowManager;
        this.mKeyguardDelegate = keyguardDelegate;
        this.mPowerManager = powerManager;
    }

    /* access modifiers changed from: protected */
    public void enableUserActivity(boolean value) {
    }

    public boolean isShowing() {
        return this.mKeyguardDelegate.isShowing();
    }

    public boolean isShowingAndNotHidden() {
        return isShowing() && !this.mPhoneWindowManager.mKeyguardOccluded;
    }

    public void keyguardDone() {
    }

    public boolean onWakeKeyWhenKeyguardShowingTq(int keyCode, boolean isDocked) {
        return false;
    }

    public void onScreenTurnedOnWithoutListener() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.onScreenTurnedOn();
        }
    }

    public void pokeWakelock() {
        this.mPowerManager.userActivity(SystemClock.uptimeMillis(), true);
    }

    public void OnDoubleClickHome() {
        KeyguardServiceDelegate keyguardServiceDelegate = this.mKeyguardDelegate;
        if (keyguardServiceDelegate != null) {
            keyguardServiceDelegate.OnDoubleClickHome();
        }
    }
}
