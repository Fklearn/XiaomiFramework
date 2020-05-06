package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.o.g.c;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.dual.Sim;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.PrivacyDeclareAndAllowNetworkUtil;
import com.miui.networkassistant.utils.TelephonyUtil;
import com.miui.networkassistant.webapi.PurchaseSmsNumberResult;
import com.miui.networkassistant.webapi.WebApiAccessHelper;
import java.util.ArrayList;

public class PurchaseSmsManager {
    private final String TAG = "PurchaseSmsManager";
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    private Context mContext;
    /* access modifiers changed from: private */
    public PurchaseSmsNumberResult mPurchaseSmsNumberResult;
    /* access modifiers changed from: private */
    public ArrayList<String> mSmsNumberWhiteList;

    public PurchaseSmsManager(Context context) {
        this.mContext = context;
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
        this.mPurchaseSmsNumberResult = new PurchaseSmsNumberResult(this.mCommonConfig.getPurchaseSmsNumber());
        this.mSmsNumberWhiteList = this.mPurchaseSmsNumberResult.getSmsNumberList();
    }

    private int getSlotIdExtra(Intent intent, int i) {
        c.a a2 = c.a.a("miui.telephony.SubscriptionManager");
        a2.b("getSlotIdExtra", new Class[]{Intent.class, Integer.TYPE}, intent, Integer.valueOf(i));
        return a2.c();
    }

    /* access modifiers changed from: package-private */
    public boolean checkContainReceiveNumber(Intent intent) {
        SmsMessage[] messagesFromIntent = TelephonyUtil.getMessagesFromIntent(intent);
        if (messagesFromIntent != null && messagesFromIntent.length > 0) {
            String removePhoneNumPrefix = TelephonyUtil.removePhoneNumPrefix(messagesFromIntent[0].getDisplayOriginatingAddress(), "+86");
            ArrayList<String> arrayList = this.mSmsNumberWhiteList;
            if (arrayList != null && arrayList.size() > 0 && !TextUtils.isEmpty(removePhoneNumPrefix)) {
                Log.i("PurchaseSmsManager", "mina checkContainReceiveNumber");
                return this.mSmsNumberWhiteList.contains(removePhoneNumPrefix);
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void checkPurchaseSmsNumberWhiteList() {
        if (!DeviceUtil.IS_INTERNATIONAL_BUILD && PrivacyDeclareAndAllowNetworkUtil.isAllowNetwork() && System.currentTimeMillis() > this.mCommonConfig.getPurchaseSmsNumberUpdateTime()) {
            a.a(new Runnable() {
                public void run() {
                    PurchaseSmsNumberResult unused = PurchaseSmsManager.this.mPurchaseSmsNumberResult = WebApiAccessHelper.updatePurchaseSmsNumberWhiteList();
                    if (PurchaseSmsManager.this.mPurchaseSmsNumberResult.isSuccess()) {
                        PurchaseSmsManager.this.mCommonConfig.setPurchaseSmsNumber(PurchaseSmsManager.this.mPurchaseSmsNumberResult.getSmsNumberJson());
                        PurchaseSmsManager purchaseSmsManager = PurchaseSmsManager.this;
                        ArrayList unused2 = purchaseSmsManager.mSmsNumberWhiteList = purchaseSmsManager.mPurchaseSmsNumberResult.getSmsNumberList();
                        PurchaseSmsManager.this.mCommonConfig.setPurchaseSmsNumberUpdateTime(System.currentTimeMillis() + 604800000);
                        Log.i("PurchaseSmsManager", "update purchase sms number white list");
                    }
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public int getSlotIdFromIntent(Intent intent) {
        int i = 0;
        if (Sim.MAX_SLOT_COUNT > 1 && (i = getSlotIdExtra(intent, 0)) < 0) {
            Log.i("PurchaseSmsManager", "getSlotIdFromIntent slotId < 0");
        }
        return i;
    }
}
