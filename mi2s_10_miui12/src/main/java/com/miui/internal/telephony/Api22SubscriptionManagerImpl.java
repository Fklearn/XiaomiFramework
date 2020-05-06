package com.miui.internal.telephony;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;
import miui.telephony.SubscriptionInfo;
import miui.telephony.SubscriptionManager;
import miui.telephony.phonenumber.Prefix;
import miui.util.AppConstants;

/* compiled from: SubscriptionManagerAndroidImpl */
class Api22SubscriptionManagerImpl extends Api21SubscriptionManagerImpl {
    protected Object mSubscriptionManager;

    public Api22SubscriptionManagerImpl() {
        try {
            this.mSubscriptionManager = sSubscriptionManagerCls.getDeclaredMethod("from", new Class[]{Context.class}).invoke((Object) null, new Object[]{AppConstants.getCurrentApplication()});
        } catch (Exception e) {
            logException(e);
        }
    }

    /* access modifiers changed from: protected */
    public List<SubscriptionInfo> getAllSubscriptionInfoListInternal() {
        List<SubscriptionInfo> subInfoList = new ArrayList<>();
        for (Object subscriptionInfo : getAllAndroidSubscriptionInfoList()) {
            subInfoList.add(new Api22SubscriptionInfoImpl(subscriptionInfo));
        }
        return subInfoList;
    }

    private List<?> getAllAndroidSubscriptionInfoList() {
        try {
            return (List) sSubscriptionManagerCls.getDeclaredMethod("getAllSubscriptionInfoList", new Class[0]).invoke(this.mSubscriptionManager, new Object[0]);
        } catch (Exception e) {
            logException(e);
            return new ArrayList();
        }
    }

    /* access modifiers changed from: protected */
    public int getDefaultSubId() {
        try {
            return ((Integer) sSubscriptionManagerCls.getDeclaredMethod("getDefaultSubId", new Class[0]).invoke((Object) null, new Object[0])).intValue();
        } catch (Exception e) {
            logException(e);
            return super.getDefaultSubId();
        }
    }

    /* access modifiers changed from: protected */
    public int getSlotId(int subId) {
        try {
            return ((Integer) sSubscriptionManagerCls.getDeclaredMethod("getSlotId", new Class[]{Integer.TYPE}).invoke((Object) null, new Object[]{Integer.valueOf(subId)})).intValue();
        } catch (Exception e) {
            logException(e);
            return super.getSlotId(subId);
        }
    }

    /* compiled from: SubscriptionManagerAndroidImpl */
    private static class Api22SubscriptionInfoImpl extends SubscriptionInfo {
        private static Class sSubscriptionInfoCls;
        int mSlotId;
        private final Object mSubInfo;
        private final int mSubscriptionId;

        static {
            try {
                sSubscriptionInfoCls = Class.forName("android.telephony.SubscriptionInfo");
            } catch (ClassNotFoundException e) {
                BaseSubscriptionManagerImpl.logException(e);
            }
        }

        private Api22SubscriptionInfoImpl(Object si) {
            this.mSubInfo = si;
            int subscriptionId = getSubscriptionIdInternal();
            int simSlotIndex = getSimSlotIndexInternal();
            this.mSubscriptionId = SubscriptionManager.isValidSubscriptionId(subscriptionId) ? subscriptionId : SubscriptionManager.INVALID_SUBSCRIPTION_ID;
            this.mSlotId = SubscriptionManager.isValidSlotId(simSlotIndex) ? simSlotIndex : SubscriptionManager.INVALID_SLOT_ID;
        }

        private int getSubscriptionIdInternal() {
            try {
                return ((Integer) sSubscriptionInfoCls.getDeclaredMethod("getSubscriptionId", new Class[0]).invoke(this.mSubInfo, new Object[0])).intValue();
            } catch (Exception e) {
                BaseSubscriptionManagerImpl.logException(e);
                return SubscriptionManager.INVALID_SUBSCRIPTION_ID;
            }
        }

        private int getSimSlotIndexInternal() {
            try {
                return ((Integer) sSubscriptionInfoCls.getDeclaredMethod("getSimSlotIndex", new Class[0]).invoke(this.mSubInfo, new Object[0])).intValue();
            } catch (Exception e) {
                BaseSubscriptionManagerImpl.logException(e);
                return SubscriptionManager.INVALID_SLOT_ID;
            }
        }

        public int getSubscriptionId() {
            return this.mSubscriptionId;
        }

        public String getIccId() {
            try {
                return (String) sSubscriptionInfoCls.getDeclaredMethod("getIccId", new Class[0]).invoke(this.mSubInfo, new Object[0]);
            } catch (Exception e) {
                BaseSubscriptionManagerImpl.logException(e);
                return Prefix.EMPTY;
            }
        }

        public int getSlotId() {
            return this.mSlotId;
        }

        public int getPhoneId() {
            return this.mSlotId;
        }

        public CharSequence getDisplayName() {
            try {
                return (String) sSubscriptionInfoCls.getDeclaredMethod("getDisplayName", new Class[0]).invoke(this.mSubInfo, new Object[0]);
            } catch (Exception e) {
                BaseSubscriptionManagerImpl.logException(e);
                return Prefix.EMPTY;
            }
        }

        public String getDisplayNumber() {
            try {
                return (String) sSubscriptionInfoCls.getDeclaredMethod("getNumber", new Class[0]).invoke(this.mSubInfo, new Object[0]);
            } catch (Exception e) {
                BaseSubscriptionManagerImpl.logException(e);
                return Prefix.EMPTY;
            }
        }

        public boolean isActivated() {
            return false;
        }

        public int getMcc() {
            try {
                return ((Integer) sSubscriptionInfoCls.getDeclaredMethod("getMcc", new Class[0]).invoke(this.mSubInfo, new Object[0])).intValue();
            } catch (Exception e) {
                BaseSubscriptionManagerImpl.logException(e);
                return super.getMcc();
            }
        }

        public int getMnc() {
            try {
                return ((Integer) sSubscriptionInfoCls.getDeclaredMethod("getMnc", new Class[0]).invoke(this.mSubInfo, new Object[0])).intValue();
            } catch (Exception e) {
                BaseSubscriptionManagerImpl.logException(e);
                return super.getMnc();
            }
        }
    }
}
