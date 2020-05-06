package com.miui.luckymoney.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.miui.luckymoney.config.CommonConfig;
import com.miui.luckymoney.ui.activity.LuckySettingActivity;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import org.json.JSONObject;

public class LuckyPushUtil {
    private static final int NOTI_ID_LUCKYMONEY = 1;

    public static void processCMD(Context context, JSONObject jSONObject) {
        if (!CommonConfig.getInstance(context).getXiaomiLuckyMoneyEnable()) {
            String optString = jSONObject.optString("cmd");
            String optString2 = jSONObject.optString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME);
            String optString3 = jSONObject.optString("summary");
            PendingIntent activity = PendingIntent.getActivity(context, 0, new Intent(context, LuckySettingActivity.class), 134217728);
            if ("notifyNow".equals(optString)) {
                NotificationUtil.showPushNotification(context, 1, activity, optString2, optString3);
            }
        }
    }
}
