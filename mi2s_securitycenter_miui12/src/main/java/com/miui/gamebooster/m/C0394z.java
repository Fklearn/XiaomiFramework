package com.miui.gamebooster.m;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;

/* renamed from: com.miui.gamebooster.m.z  reason: case insensitive filesystem */
class C0394z implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f4532a;

    C0394z(Context context) {
        this.f4532a = context;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster a2 = IGameBooster.Stub.a(iBinder);
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(a2 == null);
        Log.i("GameBoxFunctionUtils", sb.toString());
        if (a2 != null) {
            try {
                a2.b(8);
            } catch (RemoteException e) {
                Log.i("GameBoxFunctionUtils", e.toString());
            }
        }
        C0390v.a(this.f4532a).a();
        return false;
    }
}
