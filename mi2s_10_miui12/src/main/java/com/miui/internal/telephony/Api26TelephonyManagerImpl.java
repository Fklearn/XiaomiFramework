package com.miui.internal.telephony;

import android.telephony.TelephonyManager;

/* compiled from: TelephonyManagerAndroidImpl */
class Api26TelephonyManagerImpl extends Api24TelephonyManagerImpl {
    Api26TelephonyManagerImpl() {
    }

    public String getMeid() {
        try {
            return (String) TelephonyManager.class.getDeclaredMethod("getMeid", new Class[0]).invoke(this.mTelephonyManager, new Object[0]);
        } catch (Exception e) {
            logException(e);
            return super.getMeid();
        }
    }

    public String getMeidForSlot(int slotId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getMeid", new Class[]{Integer.TYPE}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(slotId)});
        } catch (Exception e) {
            logException(e);
            return super.getMeidForSlot(slotId);
        }
    }
}
