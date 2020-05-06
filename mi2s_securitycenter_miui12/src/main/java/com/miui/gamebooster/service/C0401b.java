package com.miui.gamebooster.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

/* renamed from: com.miui.gamebooster.service.b  reason: case insensitive filesystem */
class C0401b extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterService f4806a;

    C0401b(GameBoosterService gameBoosterService) {
        this.f4806a = gameBoosterService;
    }

    public void onReceive(Context context, Intent intent) {
        if (intent != null && !TextUtils.isEmpty(intent.getAction()) && "gb.action.update_game_list".equals(intent.getAction()) && this.f4806a.i != null) {
            this.f4806a.i.sendEmptyMessage(126);
        }
    }
}
