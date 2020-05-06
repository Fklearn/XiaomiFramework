package com.miui.internal.telephony;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import java.lang.reflect.Field;
import miui.telephony.phonenumber.Prefix;

/* compiled from: TelephonyManagerAndroidImpl */
class Api21TelephonyManagerImpl extends BaseTelephonyManagerAndroidImpl {
    private String mMiuiDeviceId;

    Api21TelephonyManagerImpl() {
    }

    /* access modifiers changed from: protected */
    public Class getClassForSubId() {
        return Long.TYPE;
    }

    public int getPhoneCount() {
        try {
            return ((Integer) TelephonyManager.class.getDeclaredMethod("getPhoneCount", new Class[0]).invoke(this.mTelephonyManager, new Object[0])).intValue();
        } catch (Exception e) {
            logException(e);
            return 1;
        }
    }

    public String getDeviceIdForSlot(int slotId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getDeviceId", new Class[]{Integer.TYPE}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(slotId)});
        } catch (Exception e) {
            logException(e);
            return super.getDeviceIdForSlot(slotId);
        }
    }

    public String getMiuiDeviceId() {
        if (TextUtils.isEmpty(this.mMiuiDeviceId)) {
            if (getPhoneCount() < 2) {
                this.mMiuiDeviceId = getDeviceId();
            } else {
                int phoneCount = getPhoneCount();
                String deviceIdMax = Prefix.EMPTY;
                for (int i = 0; i < phoneCount; i++) {
                    String deviceId = getDeviceIdForSlot(i);
                    if (!TextUtils.isEmpty(deviceId) && deviceId.compareTo(deviceIdMax) > 0) {
                        deviceIdMax = deviceId;
                    }
                }
                this.mMiuiDeviceId = deviceIdMax;
            }
        }
        return this.mMiuiDeviceId;
    }

    public String getImei() {
        try {
            return (String) TelephonyManager.class.getDeclaredMethod("getImei", new Class[0]).invoke(this.mTelephonyManager, new Object[0]);
        } catch (Exception e) {
            logException(e);
            return super.getImei();
        }
    }

    public String getImeiForSlot(int slotId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getImei", new Class[]{Integer.TYPE}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(slotId)});
        } catch (Exception e) {
            logException(e);
            return super.getImeiForSlot(slotId);
        }
    }

    public int getPhoneTypeForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return ((Integer) cls.getDeclaredMethod("getCurrentPhoneType", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)})).intValue();
        } catch (Exception e) {
            logException(e);
            return super.getPhoneTypeForSubscription(subId);
        }
    }

    public String getLine1NumberForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getLine1NumberForSubscriber", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getLine1NumberForSubscription(subId);
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

    public boolean hasIccCard(int slotId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return ((Boolean) cls.getDeclaredMethod("hasIccCard", new Class[]{Long.TYPE}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(slotId)})).booleanValue();
        } catch (Exception e) {
            logException(e);
            return super.hasIccCard(slotId);
        }
    }

    public String getSimOperatorForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getSimOperator", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getSimOperatorForSubscription(subId);
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

    public int getSimStateForSlot(int slotId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return ((Integer) cls.getDeclaredMethod("getSimState", new Class[]{Integer.TYPE}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(slotId)})).intValue();
        } catch (Exception e) {
            logException(e);
            return super.getSimStateForSlot(slotId);
        }
    }

    public String getSimSerialNumberForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getSimSerialNumber", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getSimSerialNumberForSubscription(subId);
        }
    }

    public String getSubscriberIdForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getSubscriberId", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getSubscriberIdForSubscription(subId);
        }
    }

    public String getNetworkOperatorNameForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getNetworkOperatorName", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getNetworkOperatorNameForSubscription(subId);
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

    public int getNetworkTypeForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return ((Integer) cls.getDeclaredMethod("getNetworkType", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)})).intValue();
        } catch (Exception e) {
            logException(e);
            return super.getNetworkTypeForSubscription(subId);
        }
    }

    public String getSimCountryIsoForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getSimCountryIso", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getSimCountryIsoForSubscription(subId);
        }
    }

    public String getVoiceMailNumberForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getVoiceMailNumber", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getVoiceMailNumberForSubscription(subId);
        }
    }

    public String getVoiceMailAlphaTagForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return (String) cls.getDeclaredMethod("getVoiceMailNumber", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)});
        } catch (Exception e) {
            logException(e);
            return super.getVoiceMailAlphaTagForSubscription(subId);
        }
    }

    public int getCallStateForSubscription(int subId) {
        Class<TelephonyManager> cls = TelephonyManager.class;
        try {
            return ((Integer) cls.getDeclaredMethod("getCallState", new Class[]{getClassForSubId()}).invoke(this.mTelephonyManager, new Object[]{Integer.valueOf(subId)})).intValue();
        } catch (Exception e) {
            logException(e);
            return super.getCallStateForSubscription(subId);
        }
    }

    public void listenForSubscription(int subId, PhoneStateListener listener, int events) {
        try {
            Field subIdField = PhoneStateListener.class.getDeclaredField("mSubId");
            long old = subIdField.getLong(listener);
            subIdField.setLong(listener, (long) subId);
            listen(listener, events);
            subIdField.setLong(listener, old);
        } catch (Exception e) {
            logException(e);
        }
    }
}
