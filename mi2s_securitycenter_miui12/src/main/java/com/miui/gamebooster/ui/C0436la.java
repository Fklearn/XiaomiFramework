package com.miui.gamebooster.ui;

import android.util.Log;
import com.miui.securitycenter.R;

/* renamed from: com.miui.gamebooster.ui.la  reason: case insensitive filesystem */
class C0436la implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxAlertActivity f5085a;

    C0436la(GameBoxAlertActivity gameBoxAlertActivity) {
        this.f5085a = gameBoxAlertActivity;
    }

    public void run() {
        try {
            GameBoxAlertActivity.b(this.f5085a);
            if (this.f5085a.e == 0) {
                this.f5085a.h.setEnabled(true);
                this.f5085a.h.setText(this.f5085a.getResources().getString(R.string.ok));
                return;
            }
            this.f5085a.h.setText(this.f5085a.getResources().getString(R.string.gamebox_positive_button_text, new Object[]{Integer.valueOf(this.f5085a.e)}));
            this.f5085a.f.postDelayed(this.f5085a.i, 1000);
        } catch (Exception e) {
            Log.e("GameBoosterReflectUtils", "setAlertParams", e);
        }
    }
}
