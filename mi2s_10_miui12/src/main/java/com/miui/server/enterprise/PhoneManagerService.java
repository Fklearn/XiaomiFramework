package com.miui.server.enterprise;

import android.content.Context;
import android.provider.MiuiSettings;
import android.telephony.TelephonyManager;
import android.util.Slog;
import com.miui.enterprise.IPhoneManager;
import com.miui.enterprise.settings.EnterpriseSettings;
import java.util.List;
import miui.telephony.PhoneNumberUtils;
import miui.telephony.TelephonyManagerEx;

public class PhoneManagerService extends IPhoneManager.Stub {
    private static final String TAG = "Enterprise-phone";
    private Context mContext;

    PhoneManagerService(Context context) {
        this.mContext = context;
    }

    public void controlSMS(int flags, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_sms_status", flags, userId);
    }

    public void controlPhoneCall(int flags, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_phone_call_status", flags, userId);
    }

    public void controlCellular(int flag, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_cellular_status", flag, userId);
        if (shouldOpen(flag)) {
            TelephonyManager.from(this.mContext).setDataEnabled(true);
        }
        if (shouldClose(flag)) {
            TelephonyManager.from(this.mContext).setDataEnabled(false);
        }
    }

    private boolean shouldOpen(int state) {
        return state == 2 || state == 4;
    }

    private boolean shouldClose(int state) {
        return state == 0 || state == 3;
    }

    public int getSMSStatus(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, "ep_sms_status", 0, userId);
    }

    public int getPhoneCallStatus(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, "ep_phone_call_status", 0, userId);
    }

    public int getCellularStatus(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, "ep_cellular_status", 0, userId);
    }

    public String getIMEI(int slotId) {
        ServiceUtils.checkPermission(this.mContext);
        return miui.telephony.TelephonyManager.getDefault().getImeiForSlot(slotId);
    }

    public void setPhoneCallAutoRecord(boolean isAutoRecord, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        MiuiSettings.System.putBooleanForUser(this.mContext.getContentResolver(), "button_auto_record_call", isAutoRecord, userId);
        EnterpriseSettings.putInt(this.mContext, "ep_force_auto_call_record", isAutoRecord, userId);
    }

    public void setPhoneCallAutoRecordDir(String dir) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_force_auto_call_record_dir", dir);
    }

    public boolean isAutoRecordPhoneCall(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, "ep_force_auto_call_record", 0, userId) == 1;
    }

    public void setSMSBlackList(List<String> list, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_sms_black_list", EnterpriseSettings.generateListSettings(list), userId);
    }

    public void setSMSWhiteList(List<String> list, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_sms_white_list", EnterpriseSettings.generateListSettings(list), userId);
    }

    public List<String> getSMSBlackList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_sms_black_list", userId));
    }

    public List<String> getSMSWhiteList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_sms_white_list", userId));
    }

    public void setSMSContactRestriction(int mode, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_sms_restriction_mode", mode, userId);
    }

    public int getSMSContactRestriction(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, "ep_sms_restriction_mode", 0, userId);
    }

    public void setCallBlackList(List<String> list, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_call_black_list", EnterpriseSettings.generateListSettings(list), userId);
    }

    public void setCallWhiteList(List<String> list, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putString(this.mContext, "ep_call_white_list", EnterpriseSettings.generateListSettings(list), userId);
    }

    public List<String> getCallBlackList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_call_black_list", userId));
    }

    public List<String> getCallWhiteList(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.parseListSettings(EnterpriseSettings.getString(this.mContext, "ep_call_white_list", userId));
    }

    public void setCallContactRestriction(int mode, int userId) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_call_restriction_mode", mode, userId);
    }

    public int getCallContactRestriction(int userId) {
        ServiceUtils.checkPermission(this.mContext);
        return EnterpriseSettings.getInt(this.mContext, "ep_call_restriction_mode", 0, userId);
    }

    public void endCall() {
        ServiceUtils.checkPermission(this.mContext);
        Slog.d(TAG, "End current call");
        TelephonyManagerEx.getDefault().endCall();
    }

    public void disableCallForward(boolean disabled) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_disable_call_forward", disabled);
    }

    public void disableCallLog(boolean disable) {
        ServiceUtils.checkPermission(this.mContext);
        EnterpriseSettings.putInt(this.mContext, "ep_disable_call_log", disable);
    }

    public String getAreaCode(String phoneNumber) {
        ServiceUtils.checkPermission(this.mContext);
        return PhoneNumberUtils.PhoneNumber.parse(phoneNumber).getLocationAreaCode(this.mContext);
    }

    public String getMeid(int slotId) {
        ServiceUtils.checkPermission(this.mContext);
        return miui.telephony.TelephonyManager.getDefault().getMeidForSlot(slotId);
    }

    public void setIccCardActivate(int slotId, boolean isActive) {
        ServiceUtils.checkPermission(this.mContext);
        miui.telephony.TelephonyManager.getDefault().setIccCardActivate(slotId, isActive);
    }
}
