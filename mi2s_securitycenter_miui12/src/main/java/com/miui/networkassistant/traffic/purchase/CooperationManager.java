package com.miui.networkassistant.traffic.purchase;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.a.a;
import com.miui.networkassistant.config.SimUserConstants;
import com.miui.networkassistant.config.SimUserInfo;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.LoadConfigUtil;
import com.miui.networkassistant.utils.MiSimUtil;
import com.miui.networkassistant.utils.PrivacyDeclareAndAllowNetworkUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.utils.TrafficUpdateUtil;
import com.miui.networkassistant.webapi.PurchaseOnlineResult;
import com.miui.networkassistant.webapi.WebApiAccessHelper;

public class CooperationManager {
    private static final String TAG = "CooperationManager";

    public static boolean isTrafficPurchaseAvailable(Context context, SimUserInfo simUserInfo, boolean z) {
        if (!PrivacyDeclareAndAllowNetworkUtil.isAllowNetwork() || !LoadConfigUtil.isDataUsagePurchaseEnabled(context) || MiSimUtil.isMiSimEnable(context, simUserInfo.getSlotNum())) {
            return false;
        }
        if (z) {
            updateTrafficPurchaseStatus(context.getApplicationContext(), simUserInfo, false);
        }
        boolean isNATrafficPurchaseAvailable = simUserInfo.isSimInserted() ? simUserInfo.isNATrafficPurchaseAvailable() : false;
        Log.i(TAG, String.format("mina isTrafficPurchaseAvailable:%b", new Object[]{Boolean.valueOf(isNATrafficPurchaseAvailable)}));
        return isNATrafficPurchaseAvailable;
    }

    public static void navigationToTrafficPurchasePage(Activity activity, SimUserInfo simUserInfo, String str) {
        if (simUserInfo.isNATrafficPurchaseAvailable()) {
            PurchaseUtil.launchUrl((Context) activity, str, simUserInfo.getSlotNum());
            updateTrafficPurchaseStatus(activity.getApplicationContext(), simUserInfo, true);
            return;
        }
        PurchaseUtil.launchUrl((Context) activity, str, simUserInfo.getSlotNum());
    }

    /* access modifiers changed from: private */
    public static boolean updateNaTrafficPurchaseStatus(Context context, SimUserInfo simUserInfo) {
        long j;
        Log.i(TAG, "mina updateNaTrafficPurchaseStatus");
        if (DeviceUtil.IS_INTERNATIONAL_BUILD) {
            return false;
        }
        PurchaseOnlineResult checkRichPurchaseOnlineResult = WebApiAccessHelper.checkRichPurchaseOnlineResult(String.valueOf(simUserInfo.getCity()), TelephonyUtil.getPhoneNumber(context, simUserInfo.getSlotNum()), simUserInfo.getOperator());
        if (!checkRichPurchaseOnlineResult.isResponsed() || !checkRichPurchaseOnlineResult.isSuccess()) {
            Log.i(TAG, "mina updateNaTrafficPurchaseStatus no response");
            return false;
        }
        boolean isOnline = checkRichPurchaseOnlineResult.isOnline();
        long j2 = 86400000;
        if (!isOnline) {
            j = ((long) checkRichPurchaseOnlineResult.getOldAge()) * 86400000;
            j2 = System.currentTimeMillis();
        } else {
            j = System.currentTimeMillis();
        }
        simUserInfo.saveNATrafficPurchaseOrderTips(checkRichPurchaseOnlineResult.getmOrderTips());
        simUserInfo.saveNATrafficPurchaseType(checkRichPurchaseOnlineResult.getOrderType());
        simUserInfo.saveNATrafficPurchaseAvailable(isOnline);
        simUserInfo.saveNATrafficPurchaseAvailableUpdateTime(j + j2);
        String purchaseActivityId = checkRichPurchaseOnlineResult.getPurchaseActivityId();
        if (TextUtils.isEmpty(purchaseActivityId) || TextUtils.equals(purchaseActivityId, SimUserConstants.DEFAULT.PURCHASE_ACTIVITY_ID_DEFAULT) || TextUtils.equals(purchaseActivityId, simUserInfo.getPurchaseActivityId())) {
            simUserInfo.setNATipsEnable(false);
        } else {
            simUserInfo.setNATipsEnable(true);
            simUserInfo.savePurchaseActivityId(purchaseActivityId);
        }
        TrafficUpdateUtil.broadCastTrafficUpdated(context);
        Log.i(TAG, String.format("mina updateNaTrafficPurchaseStatus updated, result:%b", new Object[]{Boolean.valueOf(isOnline)}));
        return isOnline;
    }

    private static void updateTrafficPurchaseStatus(final Context context, final SimUserInfo simUserInfo, final boolean z) {
        if (simUserInfo.isSimInserted()) {
            a.a(new Runnable() {
                public void run() {
                    Log.i(CooperationManager.TAG, "mina updateTrafficPurchaseStatus");
                    if (System.currentTimeMillis() > simUserInfo.getNATrafficPurchaseAvailableUpdateTime() || z) {
                        boolean unused = CooperationManager.updateNaTrafficPurchaseStatus(context, simUserInfo);
                    }
                }
            });
        }
    }
}
