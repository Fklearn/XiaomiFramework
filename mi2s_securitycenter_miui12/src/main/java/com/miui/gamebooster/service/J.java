package com.miui.gamebooster.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

class J extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ VideoToolBoxService f4788a;

    J(VideoToolBoxService videoToolBoxService) {
        this.f4788a = videoToolBoxService;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction()) && "gb.action.update_video_list".equals(intent.getAction()) && this.f4788a.f4799b != null) {
            this.f4788a.f4799b.sendEmptyMessage(4);
        }
    }
}
