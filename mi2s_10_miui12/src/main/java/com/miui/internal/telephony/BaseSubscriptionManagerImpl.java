package com.miui.internal.telephony;

import android.util.Log;
import java.util.List;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;

/* compiled from: SubscriptionManagerAndroidImpl */
class BaseSubscriptionManagerImpl extends SubscriptionManager {
    BaseSubscriptionManagerImpl() {
    }

    protected static void logException(Exception e) {
        Log.e("SubscriptionAndroidImpl", "method reflection failed", e);
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getAllSubscriptionInfoListInternal() {
        return null;
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getSubscriptionInfoListInternal() {
        return null;
    }

    /* access modifiers changed from: protected */
    public void addOnSubscriptionsChangedListenerInternal() {
    }

    /* access modifiers changed from: protected */
    public void removeOnSubscriptionsChangedListenerInternal() {
    }

    public int getSlotIdForSubscription(int subId) {
        return 0;
    }

    public int getSubscriptionIdForSlot(int slotId) {
        return INVALID_SUBSCRIPTION_ID;
    }

    public int getDefaultSlotId() {
        return 0;
    }

    public int getDefaultVoiceSubscriptionId() {
        return 0;
    }

    public SubscriptionInfo getDefaultVoiceSubscriptionInfo() {
        return null;
    }

    public int getDefaultVoiceSlotId() {
        return 0;
    }

    public void setDefaultVoiceSlotId(int slotId) {
    }

    public int getDefaultDataSubscriptionId() {
        return 0;
    }

    public SubscriptionInfo getDefaultDataSubscriptionInfo() {
        return null;
    }

    public int getDefaultDataSlotId() {
        return 0;
    }

    public void setDefaultDataSlotId(int slotId) {
    }

    public int getDefaultSmsSubscriptionId() {
        return 0;
    }

    public SubscriptionInfo getDefaultSmsSubscriptionInfo() {
        return null;
    }

    public void setDefaultSmsSubscriptionId(int subId) {
    }

    /* access modifiers changed from: protected */
    public int getDefaultSlotIdInternal() {
        return 0;
    }

    public List<SubscriptionInfo> getActiveSubscriptionInfoList() {
        return null;
    }

    public int getSubscriptionInfoCount() {
        return 0;
    }
}
