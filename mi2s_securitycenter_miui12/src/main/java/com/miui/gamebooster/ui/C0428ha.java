package com.miui.gamebooster.ui;

import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;

/* renamed from: com.miui.gamebooster.ui.ha  reason: case insensitive filesystem */
class C0428ha implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoosterSettingFragment f5070a;

    C0428ha(GameBoosterSettingFragment gameBoosterSettingFragment) {
        this.f5070a = gameBoosterSettingFragment;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster unused = this.f5070a.e = IGameBooster.Stub.a(iBinder);
        String a2 = GameBoosterSettingFragment.f4889a;
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(this.f5070a.e == null);
        Log.i(a2, sb.toString());
        if (this.f5070a.e != null) {
            try {
                this.f5070a.e.n();
            } catch (RemoteException e) {
                Log.i(GameBoosterSettingFragment.f4889a, e.toString());
            }
        }
        return false;
    }
}
