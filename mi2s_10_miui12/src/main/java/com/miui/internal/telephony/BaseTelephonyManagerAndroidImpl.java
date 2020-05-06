package com.miui.internal.telephony;

import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import miui.telephony.SubscriptionManager;
import miui.telephony.TelephonyManager;
import miui.util.AppConstants;

/* compiled from: TelephonyManagerAndroidImpl */
class BaseTelephonyManagerAndroidImpl extends TelephonyManager {
    protected android.telephony.TelephonyManager mTelephonyManager = ((android.telephony.TelephonyManager) AppConstants.getCurrentApplication().getSystemService("phone"));

    BaseTelephonyManagerAndroidImpl() {
    }

    protected static void logException(Exception e) {
        Log.e("TelephonyAndroidImpl", "method reflection failed", e);
    }

    public boolean isMultiSimEnabled() {
        return false;
    }

    public int getPhoneCount() {
        return 1;
    }

    public String getDeviceSoftwareVersion() {
        return this.mTelephonyManager.getDeviceSoftwareVersion();
    }

    public String getDeviceSoftwareVersionForSlot(int slotId) {
        return getDeviceSoftwareVersion();
    }

    public String getDeviceSoftwareVersionForSubscription(int subId) {
        return getDeviceSoftwareVersionForSubscription(getSlotIdForSubscription(subId));
    }

    public String getMiuiDeviceId() {
        return getDeviceId();
    }

    public String getSmallDeviceId() {
        return getDeviceId();
    }

    public List<String> getDeviceIdList() {
        ArrayList<String> deviceIdList = new ArrayList<>();
        for (int i = 0; i < getPhoneCount(); i++) {
            String deviceId = getDeviceIdForSlot(i);
            if (!TextUtils.isEmpty(deviceId) && !deviceIdList.contains(deviceId)) {
                deviceIdList.add(deviceId);
            }
            String meid = getMeidForSlot(i);
            if (!TextUtils.isEmpty(meid) && !deviceIdList.contains(meid)) {
                deviceIdList.add(meid);
            }
        }
        return deviceIdList;
    }

    public List<String> getImeiList() {
        ArrayList<String> imeiList = new ArrayList<>();
        for (int i = 0; i < getPhoneCount(); i++) {
            String imei = getImeiForSlot(i);
            if (!TextUtils.isEmpty(imei)) {
                imeiList.add(imei);
            }
        }
        return imeiList;
    }

    public List<String> getMeidList() {
        List<String> meidList = new ArrayList<>();
        for (int i = 0; i < getPhoneCount(); i++) {
            String meid = getMeidForSlot(i);
            if (!TextUtils.isEmpty(meid) && !meidList.contains(meid)) {
                meidList.add(meid);
            }
        }
        return meidList;
    }

    public String getDeviceId() {
        return this.mTelephonyManager.getDeviceId();
    }

    public String getDeviceIdForSlot(int slotId) {
        return getDeviceId();
    }

    public String getDeviceIdForSubscription(int subId) {
        return getDeviceIdForSlot(getSlotIdForSubscription(subId));
    }

    public String getImei() {
        return getDeviceId();
    }

    public String getImeiForSlot(int slotId) {
        return getImei();
    }

    public String getImeiForSubscription(int subId) {
        return getImeiForSlot(getSlotIdForSubscription(subId));
    }

    public String getMeid() {
        return null;
    }

    public String getMeidForSlot(int slotId) {
        return getMeid();
    }

    public String getMeidForSubscription(int subId) {
        return getMeidForSlot(getSlotIdForSubscription(subId));
    }

    public int getPhoneType() {
        return this.mTelephonyManager.getPhoneType();
    }

    public int getPhoneTypeForSlot(int slotId) {
        return getPhoneTypeForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public int getPhoneTypeForSubscription(int subId) {
        return getPhoneType();
    }

    public String getNetworkOperator() {
        return this.mTelephonyManager.getNetworkOperator();
    }

    public String getNetworkOperatorForSlot(int slotId) {
        return getNetworkOperatorForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getNetworkOperatorForSubscription(int subId) {
        return getNetworkOperator();
    }

    public String getNetworkOperatorName() {
        return this.mTelephonyManager.getNetworkOperatorName();
    }

    public String getNetworkOperatorNameForSlot(int slotId) {
        return getNetworkOperatorNameForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getNetworkOperatorNameForSubscription(int subId) {
        return getNetworkOperatorName();
    }

    public String getNetworkCountryIso() {
        return this.mTelephonyManager.getNetworkCountryIso();
    }

    public String getNetworkCountryIsoForSlot(int slotId) {
        return getNetworkCountryIsoForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getNetworkCountryIsoForSubscription(int subId) {
        return getNetworkCountryIso();
    }

    public int getNetworkType() {
        return this.mTelephonyManager.getNetworkType();
    }

    public int getNetworkTypeForSlot(int slotId) {
        return getNetworkTypeForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public int getNetworkTypeForSubscription(int subId) {
        return getNetworkType();
    }

    public boolean hasIccCard() {
        return this.mTelephonyManager.hasIccCard();
    }

    public boolean hasIccCard(int slotId) {
        return hasIccCard();
    }

    public int getSimState() {
        return this.mTelephonyManager.getSimState();
    }

    public int getSimStateForSlot(int slotId) {
        return getSimState();
    }

    public int getSimStateForSubscription(int subId) {
        return getSimStateForSlot(getSlotIdForSubscription(subId));
    }

    public String getSimOperator() {
        return this.mTelephonyManager.getSimOperator();
    }

    public String getSimOperatorForSlot(int slotId) {
        return getSimOperatorForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getSimOperatorForSubscription(int subId) {
        return getSimOperator();
    }

    public String getSimOperatorName() {
        return this.mTelephonyManager.getSimOperatorName();
    }

    public String getSimOperatorNameForSlot(int slotId) {
        return getSimOperatorNameForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getSimOperatorNameForSubscription(int subId) {
        return getSimOperatorName();
    }

    public String getSimCountryIso() {
        return this.mTelephonyManager.getSimCountryIso();
    }

    public String getSimCountryIsoForSlot(int slotId) {
        return getSimCountryIsoForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getSimCountryIsoForSubscription(int subId) {
        return getSimCountryIso();
    }

    public String getSimSerialNumber() {
        return this.mTelephonyManager.getSimSerialNumber();
    }

    public String getSimSerialNumberForSlot(int slotId) {
        return getSimSerialNumberForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getSimSerialNumberForSubscription(int subId) {
        return getSimSerialNumber();
    }

    public String getSubscriberId() {
        return this.mTelephonyManager.getSubscriberId();
    }

    public String getSubscriberIdForSlot(int slotId) {
        return getSubscriberIdForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getSubscriberIdForSubscription(int subId) {
        return getSubscriberId();
    }

    public String getLine1Number() {
        return this.mTelephonyManager.getLine1Number();
    }

    public String getLine1NumberForSlot(int slotId) {
        return getLine1NumberForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getLine1NumberForSubscription(int subId) {
        return getLine1Number();
    }

    public String getVoiceMailNumber() {
        return this.mTelephonyManager.getVoiceMailNumber();
    }

    public String getVoiceMailNumberForSlot(int slotId) {
        return getVoiceMailNumberForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getVoiceMailNumberForSubscription(int subId) {
        return getVoiceMailNumber();
    }

    public String getVoiceMailAlphaTag() {
        return this.mTelephonyManager.getVoiceMailAlphaTag();
    }

    public String getVoiceMailAlphaTagForSlot(int slotId) {
        return getVoiceMailAlphaTagForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public String getVoiceMailAlphaTagForSubscription(int subId) {
        return getVoiceMailAlphaTag();
    }

    public int getCallState() {
        return this.mTelephonyManager.getCallState();
    }

    public int getCallStateForSlot(int slotId) {
        return getCallStateForSubscription(getSubscriptionIdForSlot(slotId));
    }

    public int getCallStateForSubscription(int subId) {
        return getCallState();
    }

    public int getDataActivity() {
        return this.mTelephonyManager.getDataActivity();
    }

    public int getDataActivityForSlot(int slotId) {
        return getDataActivity();
    }

    public int getDataActivityForSubscription(int subId) {
        return getDataActivityForSlot(getSlotIdForSubscription(subId));
    }

    public int getDataState() {
        return this.mTelephonyManager.getDataState();
    }

    public int getDataStateForSlot(int slotId) {
        return getDataState();
    }

    public int getDataStateForSubscription(int subId) {
        return getDataStateForSlot(getSlotIdForSubscription(subId));
    }

    public void listen(PhoneStateListener listener, int events) {
        this.mTelephonyManager.listen(listener, events);
    }

    public void listenForSlot(int slotId, PhoneStateListener listener, int events) {
        listenForSubscription(getSubscriptionIdForSlot(slotId), listener, events);
    }

    public void listenForSubscription(int subId, PhoneStateListener listener, int events) {
        listen(listener, events);
    }

    public boolean isVoiceCapable() {
        try {
            return ((Boolean) android.telephony.TelephonyManager.class.getDeclaredMethod("isVoiceCapable", new Class[0]).invoke(this.mTelephonyManager, new Object[0])).booleanValue();
        } catch (Exception e) {
            logException(e);
            return false;
        }
    }

    private static int getSlotIdForSubscription(int subId) {
        return SubscriptionManager.getDefault().getSlotIdForSubscription(subId);
    }

    private static int getSubscriptionIdForSlot(int slotId) {
        return SubscriptionManager.getDefault().getSubscriptionIdForSlot(slotId);
    }

    public int getMiuiLevel(SignalStrength ss) {
        try {
            return ((Integer) SignalStrength.class.getDeclaredMethod("getLevel", new Class[0]).invoke(ss, new Object[0])).intValue();
        } catch (Exception e) {
            logException(e);
            return 0;
        }
    }

    public CellLocation getCellLocationForSlot(int slotId) {
        return null;
    }
}
