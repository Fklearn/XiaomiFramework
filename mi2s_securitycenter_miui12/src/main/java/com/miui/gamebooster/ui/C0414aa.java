package com.miui.gamebooster.ui;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import com.miui.earthquakewarning.Constants;
import com.miui.gamebooster.m.ba;
import com.miui.securitycenter.R;
import com.miui.securityscan.cards.g;

/* renamed from: com.miui.gamebooster.ui.aa  reason: case insensitive filesystem */
class C0414aa implements g.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Activity f5044a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f5045b;

    C0414aa(GameBoosterRealMainActivity gameBoosterRealMainActivity, Activity activity) {
        this.f5045b = gameBoosterRealMainActivity;
        this.f5044a = activity;
    }

    public void a(String str, int i, int i2) {
        if (Constants.SECURITY_ADD_PACKAGE.equals(str)) {
            if (i != -6) {
                if (i == 4) {
                    String l = GameBoosterRealMainActivity.f4885a;
                    Log.i(l, "Install SUCCESS:" + i);
                    ba.b(this.f5044a.getApplicationContext());
                    return;
                } else if (!(i == -3 || i == -2)) {
                    return;
                }
            }
            String l2 = GameBoosterRealMainActivity.f4885a;
            Log.i(l2, "Install FAIL:" + i);
            Toast.makeText(this.f5044a.getApplicationContext(), this.f5045b.getResources().getString(R.string.securityadd_install_fail), 0).show();
        }
    }
}
