package com.miui.networkassistant.ui.activity;

import android.os.Bundle;
import android.os.MessageQueue;
import b.b.c.c.b.a;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.networkassistant.utils.PrivacyDeclareAndAllowNetworkUtil;

public abstract class BaseStatsActivity extends a {
    /* JADX WARNING: type inference failed for: r0v0, types: [android.app.Activity, com.miui.networkassistant.ui.activity.BaseStatsActivity, b.b.c.c.b.a] */
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        postOnIdleUiThread(new MessageQueue.IdleHandler() {
            public boolean queueIdle() {
                AnalyticsHelper.trackActiveNetworkAssistant(BaseStatsActivity.this.getApplicationContext());
                return false;
            }
        });
        PrivacyDeclareAndAllowNetworkUtil.showSecurityCenterAllowNetwork(this);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        BaseStatsActivity.super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        BaseStatsActivity.super.onResume();
    }
}
