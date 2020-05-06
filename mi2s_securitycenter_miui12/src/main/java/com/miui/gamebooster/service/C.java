package com.miui.gamebooster.service;

import android.content.Intent;
import android.util.Log;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.m.C0378i;
import com.xiaomi.migameservice.IGameServiceCallback;

class C extends IGameServiceCallback.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4753a;

    C(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4753a = gameBoxWindowManagerService;
    }

    public void b(int i, String str) {
        int d2;
        Log.i("GameBoxWindowManager", "cmd " + i);
        if (i == 1 && (d2 = C0378i.d(this.f4753a.getApplicationContext(), str)) > 0) {
            Intent intent = new Intent("action_toast_wonderful_moment");
            intent.putExtra("match_md5", str);
            this.f4753a.getApplicationContext().sendBroadcast(intent);
            C0373d.a(this.f4753a.k(), d2);
        }
    }
}
