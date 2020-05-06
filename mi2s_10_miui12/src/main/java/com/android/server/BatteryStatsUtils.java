package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.android.internal.telephony.IccCardConstants;
import miui.telephony.SubscriptionManager;
import miui.telephony.SubscriptionManagerEx;
import miui.telephony.TelephonyManager;
import miui.util.AppConstants;

public final class BatteryStatsUtils {
    public static final int PHONE_COUNT = TelephonyManager.getDefault().getPhoneCount();
    /* access modifiers changed from: private */
    public static DataConnectionStatsCallBack sCallBack;
    /* access modifiers changed from: private */
    public static int sDataSlot = SubscriptionManager.getDefault().getDefaultDataSlotId();
    private static BroadcastReceiver sReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.ACTION_SUBINFO_RECORD_UPDATED".equals(action)) {
                if (BatteryStatsUtils.sCallBack != null) {
                    BatteryStatsUtils.sCallBack.onChanged(BatteryStatsUtils.getSubId(BatteryStatsUtils.sDataSlot), (IccCardConstants.State) null);
                }
            } else if ("miui.intent.action.ACTION_DEFAULT_DATA_SLOT_CHANGED".equals(action)) {
                int unused = BatteryStatsUtils.sDataSlot = SubscriptionManager.getDefault().getDefaultDataSlotId();
                if (BatteryStatsUtils.sCallBack != null) {
                    BatteryStatsUtils.sCallBack.onChanged(SubscriptionManager.getSubscriptionIdExtra(intent, SubscriptionManager.INVALID_SUBSCRIPTION_ID), BatteryStatsUtils.getSimState(BatteryStatsUtils.sDataSlot));
                }
            }
        }
    };

    static {
        if (PHONE_COUNT > 1) {
            AppConstants.getCurrentApplication().registerReceiver(sReceiver, new IntentFilter("miui.intent.action.ACTION_DEFAULT_DATA_SLOT_CHANGED"));
        }
    }

    public static class DataConnectionStatsCallBack {
        public void onChanged(int dataSubId, IccCardConstants.State simState) {
        }
    }

    public static void setCallBack(DataConnectionStatsCallBack callBack) {
        sCallBack = callBack;
    }

    public static boolean isDataSlotIntent(Intent intent) {
        return PHONE_COUNT < 2 || (intent != null && sDataSlot == SubscriptionManagerEx.getSlotIdExtra(intent, SubscriptionManagerEx.INVALID_SLOT_ID));
    }

    public static boolean isDataSlot(int dataSlot) {
        return PHONE_COUNT < 2 || sDataSlot == dataSlot;
    }

    public static boolean isDataSlotSub(int subId) {
        return PHONE_COUNT < 2;
    }

    public static int getDataSlot() {
        return sDataSlot;
    }

    public static int getSubId(int slotId) {
        int[] dataSub = android.telephony.SubscriptionManager.getSubId(slotId);
        if (dataSub == null || dataSub.length <= 0) {
            return -1;
        }
        return dataSub[0];
    }

    /* access modifiers changed from: private */
    public static IccCardConstants.State getSimState(int slotId) {
        int simState = TelephonyManager.getDefault().getSimStateForSlot(slotId);
        if (simState == 1) {
            return IccCardConstants.State.ABSENT;
        }
        if (simState == 5) {
            return IccCardConstants.State.READY;
        }
        if (simState == 2) {
            return IccCardConstants.State.PIN_REQUIRED;
        }
        if (simState == 3) {
            return IccCardConstants.State.PUK_REQUIRED;
        }
        if (simState == 4 || simState == 7) {
            return IccCardConstants.State.NETWORK_LOCKED;
        }
        return IccCardConstants.State.UNKNOWN;
    }
}
