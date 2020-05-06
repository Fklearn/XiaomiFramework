package com.miui.gamebooster.service;

import android.os.IBinder;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;

class G implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GameBoxWindowManagerService f4757a;

    G(GameBoxWindowManagerService gameBoxWindowManagerService) {
        this.f4757a = gameBoxWindowManagerService;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster unused = this.f4757a.k = IGameBooster.Stub.a(iBinder);
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(this.f4757a.k == null);
        Log.i("GameBoxWindowManager", sb.toString());
        return false;
    }
}
