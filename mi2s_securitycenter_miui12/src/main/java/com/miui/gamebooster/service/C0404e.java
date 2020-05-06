package com.miui.gamebooster.service;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import com.miui.common.persistence.b;
import java.util.ArrayList;

/* renamed from: com.miui.gamebooster.service.e  reason: case insensitive filesystem */
class C0404e extends Handler {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterService f4811a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0404e(GameBoosterService gameBoosterService, Looper looper) {
        super(looper);
        this.f4811a = gameBoosterService;
    }

    public void handleMessage(Message message) {
        int i = message.what;
        if (i == 122) {
            this.f4811a.p.sendBroadcast(new Intent("action_toast_booster_success"));
        } else if (i == 128) {
            GameBoosterService gameBoosterService = this.f4811a;
            gameBoosterService.a(gameBoosterService.p, true);
        } else if (i == 125) {
            GameBoosterService gameBoosterService2 = this.f4811a;
            r.a((Context) gameBoosterService2, gameBoosterService2.i).k();
        } else if (i != 126) {
            switch (i) {
                case 115:
                    if (this.f4811a.A) {
                        return;
                    }
                    break;
                case 116:
                    if (!this.f4811a.A) {
                        GameBoosterService gameBoosterService3 = this.f4811a;
                        r.a((Context) gameBoosterService3, gameBoosterService3.i).o();
                        return;
                    }
                    GameBoosterService gameBoosterService4 = this.f4811a;
                    gameBoosterService4.a(false, gameBoosterService4.p);
                    return;
                case 117:
                    this.f4811a.i();
                    return;
                case 118:
                    Log.i("GameBoosterService", "EVENT_GAME_START_DELAY: isBound=" + this.f4811a.A);
                    GameBoosterService gameBoosterService5 = this.f4811a;
                    if (!r.a((Context) gameBoosterService5, gameBoosterService5.i).d() || this.f4811a.A) {
                        return;
                    }
                case 119:
                    this.f4811a.m();
                    return;
                default:
                    return;
            }
            GameBoosterService gameBoosterService6 = this.f4811a;
            r.a((Context) gameBoosterService6, gameBoosterService6.i).m();
        } else {
            synchronized (this.f4811a.z) {
                this.f4811a.r.clear();
                this.f4811a.r.addAll(b.a("gb_added_games", (ArrayList<String>) new ArrayList()));
                int size = this.f4811a.r.size();
                if (size > 0) {
                    r.a((Context) this.f4811a, this.f4811a.i).a((String[]) this.f4811a.r.toArray(new String[size]));
                }
            }
        }
    }
}
