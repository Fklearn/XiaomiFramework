package com.miui.internal.telephony;

/* compiled from: SubscriptionManagerAndroidImpl */
class Api24SubscriptionManagerImpl extends Api22SubscriptionManagerImpl {
    Api24SubscriptionManagerImpl() {
    }

    /* access modifiers changed from: protected */
    public int getDefaultSubId() {
        try {
            return ((Integer) sSubscriptionManagerCls.getDeclaredMethod("getDefaultSubscriptionId", new Class[0]).invoke((Object) null, new Object[0])).intValue();
        } catch (Exception e) {
            logException(e);
            return super.getDefaultSubId();
        }
    }
}
