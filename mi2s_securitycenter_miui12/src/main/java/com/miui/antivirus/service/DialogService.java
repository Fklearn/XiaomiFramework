package com.miui.antivirus.service;

import android.app.IntentService;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.text.TextUtils;

public class DialogService extends IntentService {
    public DialogService() {
        super("DialogService");
    }

    /* access modifiers changed from: protected */
    public void onHandleIntent(Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
            String action = intent.getAction();
            if ("com.miui.safepay.SHOW_WARNING_DIALOG".equals(action)) {
                new Handler(getMainLooper()).post(new a(this, intent.getIntExtra("extra_risk_priority", 0), intent.getIntegerArrayListExtra("extra_risk_priority_all")));
            } else if ("com.miui.safepay.SHOW_WIFI_WARNING_DIALOG".equals(action)) {
                new Handler(getMainLooper()).post(new b(this, (WifiInfo) intent.getParcelableExtra("extra_wifi_info")));
            }
        }
    }
}
