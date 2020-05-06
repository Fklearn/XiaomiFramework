package com.miui.gamebooster.m;

import android.content.Context;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.service.IGameBooster;
import java.util.ArrayList;
import java.util.List;

class T implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ArrayList f4459a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f4460b;

    T(ArrayList arrayList, Context context) {
        this.f4459a = arrayList;
        this.f4460b = context;
    }

    public boolean a(IBinder iBinder) {
        IGameBooster a2 = IGameBooster.Stub.a(iBinder);
        StringBuilder sb = new StringBuilder();
        sb.append("gameBooster :");
        sb.append(a2 == null);
        Log.i("PermissionUtils", sb.toString());
        if (a2 != null) {
            try {
                a2.b((List<String>) this.f4459a);
            } catch (RemoteException e) {
                Log.i("PermissionUtils", e.toString());
            }
        }
        C0390v.a(this.f4460b).a();
        return false;
    }
}
