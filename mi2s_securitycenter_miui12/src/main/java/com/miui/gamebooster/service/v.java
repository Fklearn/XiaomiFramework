package com.miui.gamebooster.service;

import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.text.TextUtils;
import android.util.Log;
import com.miui.gamebooster.service.GameBoosterTelecomManager;

class v extends PhoneStateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterTelecomManager f4838a;

    v(GameBoosterTelecomManager gameBoosterTelecomManager) {
        this.f4838a = gameBoosterTelecomManager;
    }

    public void onCallStateChanged(int i, String str) {
        if (!TextUtils.isEmpty(str)) {
            Log.d("GameBoosterTeleManager", "onPhoneStateChanged state :" + i);
            if (i == 0) {
                if (this.f4838a.f4765c != null) {
                    this.f4838a.f4765c.b();
                    GameBoosterTelecomManager.b unused = this.f4838a.f4765c = null;
                }
                if (!this.f4838a.f4764b) {
                    this.f4838a.stopSelf();
                }
            } else if (i == 2) {
                if (this.f4838a.f4765c == null) {
                    GameBoosterTelecomManager gameBoosterTelecomManager = this.f4838a;
                    GameBoosterTelecomManager.b unused2 = gameBoosterTelecomManager.f4765c = new GameBoosterTelecomManager.b(gameBoosterTelecomManager.getApplication(), str);
                    this.f4838a.f4765c.c();
                }
                if (this.f4838a.f4764b) {
                    this.f4838a.f4765c.b(SystemClock.uptimeMillis());
                    this.f4838a.f4765c.d();
                }
            }
        }
    }
}
