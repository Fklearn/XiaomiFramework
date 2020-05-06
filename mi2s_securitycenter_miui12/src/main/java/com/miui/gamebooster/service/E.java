package com.miui.gamebooster.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.gamebooster.m.C0383n;
import com.miui.gamebooster.videobox.utils.a;
import com.miui.networkassistant.config.Constants;
import com.miui.securitycenter.R;

class E extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4755a;

    E(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4755a = gameBoxWindowManagerService;
    }

    public void onReceive(Context context, Intent intent) {
        GameBoxWindowManagerService gameBoxWindowManagerService;
        int i;
        String action = intent.getAction();
        if ("com.miui.FREEFORM_WINDOW_CLOSED".equals(action) && this.f4755a.f4774c.f() && !C0383n.a()) {
            Log.i("GameBoxWindowManager", intent.getStringExtra("window_name"));
            this.f4755a.n();
        } else if ("com.miui.securitycenter.intent.action.NOTIFY_DIVING_MODE_EXCEPTION".equals(action)) {
            String stringExtra = intent.getStringExtra("reason");
            if (stringExtra != null) {
                if (stringExtra.startsWith("StartFailFor")) {
                    gameBoxWindowManagerService = this.f4755a;
                    i = R.string.notify_diving_mode_exception_open_fail;
                } else if (stringExtra.startsWith("StopFailFor")) {
                    this.f4755a.a(context, R.string.notify_diving_mode_exception_close_fail, true);
                } else if (stringExtra.startsWith("ExitFor")) {
                    gameBoxWindowManagerService = this.f4755a;
                    i = R.string.notify_diving_mode_exception_close;
                }
                gameBoxWindowManagerService.a(context, i, false);
            }
            Log.i("GameBoxWindowManager", stringExtra);
        } else {
            if (Constants.System.ACTION_USER_PRESENT.equals(action) && this.f4755a.h == 3) {
                GameBoxWindowManagerService gameBoxWindowManagerService2 = this.f4755a;
                if (gameBoxWindowManagerService2.e && gameBoxWindowManagerService2.g != null) {
                    this.f4755a.g.postDelayed(this.f4755a.o, 50);
                    boolean unused = this.f4755a.m = true;
                    return;
                }
            }
            if (Constants.System.ACTION_SCREEN_OFF.equals(action)) {
                GameBoxWindowManagerService gameBoxWindowManagerService3 = this.f4755a;
                if (gameBoxWindowManagerService3.e && gameBoxWindowManagerService3.m && this.f4755a.h == 3) {
                    a.a(0);
                    boolean unused2 = this.f4755a.m = false;
                    return;
                }
            }
            if ("miui.intent.TAKE_SCREENSHOT".equals(action)) {
                this.f4755a.a(intent);
            }
        }
    }
}
