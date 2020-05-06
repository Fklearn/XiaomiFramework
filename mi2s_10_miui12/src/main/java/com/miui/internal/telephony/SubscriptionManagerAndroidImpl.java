package com.miui.internal.telephony;

import android.os.Build;
import miui.telephony.SubscriptionManager;

public class SubscriptionManagerAndroidImpl {
    static final String TAG = "SubscriptionAndroidImpl";

    private SubscriptionManagerAndroidImpl() {
    }

    public static SubscriptionManager getDefault() {
        if (Build.VERSION.SDK_INT >= 26) {
            return new Api26SubscriptionManagerImpl();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            return new Api24SubscriptionManagerImpl();
        }
        if (Build.VERSION.SDK_INT >= 22) {
            return new Api22SubscriptionManagerImpl();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            return new Api21SubscriptionManagerImpl();
        }
        return new BaseSubscriptionManagerImpl();
    }
}
