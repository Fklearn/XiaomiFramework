package com.miui.gamebooster.xunyou;

import android.util.Log;
import b.b.c.c.a.a;
import com.miui.gamebooster.k.b;
import com.miui.gamebooster.service.MiuiVpnManageServiceCallback;
import com.miui.gamebooster.ui.GameBoosterRealMainActivity;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainMiuiVpnManageServiceCallback extends MiuiVpnManageServiceCallback {

    /* renamed from: a  reason: collision with root package name */
    public static final String f5393a = "MainMiuiVpnManageServiceCallback";

    /* renamed from: b  reason: collision with root package name */
    private final WeakReference<GameBoosterRealMainActivity> f5394b;

    public MainMiuiVpnManageServiceCallback(GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f5394b = new WeakReference<>(gameBoosterRealMainActivity);
    }

    public boolean isVpnConnected() {
        return super.isVpnConnected();
    }

    public void onQueryCouponsResult(int i, List<String> list) {
        Object obj;
        int i2;
        d dVar;
        super.onQueryCouponsResult(i, list);
        GameBoosterRealMainActivity gameBoosterRealMainActivity = (GameBoosterRealMainActivity) this.f5394b.get();
        if (gameBoosterRealMainActivity != null) {
            if (i == 0) {
                b.b().a((ArrayList<String>) new ArrayList(list));
                dVar = gameBoosterRealMainActivity.j;
                i2 = 124;
                obj = new Object();
            } else {
                dVar = gameBoosterRealMainActivity.j;
                i2 = 123;
                obj = new Object();
            }
            dVar.a(i2, obj);
            String str = f5393a;
            Log.i(str, i + " gift:" + list);
        }
    }

    public void onVpnStateChanged(int i, int i2, String str) {
        super.onVpnStateChanged(i, i2, str);
        GameBoosterRealMainActivity gameBoosterRealMainActivity = (GameBoosterRealMainActivity) this.f5394b.get();
        if (gameBoosterRealMainActivity != null && gameBoosterRealMainActivity.g != null) {
            a.a(new e(this, i2, gameBoosterRealMainActivity));
            String str2 = f5393a;
            Log.i(str2, "VpnType:" + i + " " + "VpnState:" + i2 + " " + "Vpndata:" + str);
        }
    }
}
