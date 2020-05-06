package com.miui.gamebooster.mutiwindow;

import android.os.IBinder;
import android.util.Log;
import b.b.c.f.a;
import com.miui.gamebooster.mutiwindow.f;
import com.miui.gamebooster.service.IFreeformWindow;
import java.util.List;

class e implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ List f4631a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ f.a f4632b;

    e(f.a aVar, List list) {
        this.f4632b = aVar;
        this.f4631a = list;
    }

    public boolean a(IBinder iBinder) {
        IFreeformWindow a2 = IFreeformWindow.Stub.a(iBinder);
        if (a2 != null) {
            try {
                a2.d(this.f4631a);
            } catch (Exception e) {
                Log.e("FreeformWindowUtils", "set quickreply apps error when app added", e);
            }
        }
        a.a(this.f4632b.f4635a).a();
        return false;
    }
}
