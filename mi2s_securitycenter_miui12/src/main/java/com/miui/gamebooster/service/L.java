package com.miui.gamebooster.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.miui.networkassistant.config.Constants;

class L extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VideoToolBoxService f4790a;

    L(VideoToolBoxService videoToolBoxService) {
        this.f4790a = videoToolBoxService;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.equals(action, Constants.System.ACTION_SCREEN_OFF)) {
            if (TextUtils.equals(action, Constants.System.ACTION_USER_PRESENT)) {
                this.f4790a.f4799b.sendEmptyMessage(5);
            } else {
                TextUtils.equals("android.intent.action.CLOSE_SYSTEM_DIALOGS", action);
            }
        }
    }
}
