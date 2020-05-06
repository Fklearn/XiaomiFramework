package com.miui.gamebooster.ui;

import android.os.IBinder;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;

class Ta implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ WelcomActivity f5008a;

    Ta(WelcomActivity welcomActivity) {
        this.f5008a = welcomActivity;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster unused = this.f5008a.h = IGameBooster.Stub.a(iBinder);
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(this.f5008a.h == null);
        Log.i("WelcomActivity", sb.toString());
        return false;
    }
}
