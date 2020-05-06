package com.miui.internal.telephony;

import android.telephony.TelephonyManager;

/* compiled from: TelephonyManagerAndroidImpl */
class Api24TelephonyManagerImpl extends Api23TelephonyManagerImpl {
    Api24TelephonyManagerImpl() {
    }

    public String getLine1NumberForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getLine1Number", new Class[]{Integer.TYPE}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getLine1NumberForSubscription(subId);
        }
    }

    public String getSimOperatorNameForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getSimOperatorName", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getSimOperatorNameForSubscription(subId);
        }
    }

    public String getNetworkOperatorForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getNetworkOperator", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getNetworkOperatorForSubscription(subId);
        }
    }

    public String getNetworkCountryIsoForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getNetworkCountryIso", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getNetworkCountryIsoForSubscription(subId);
        }
    }
}
