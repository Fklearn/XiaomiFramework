package com.miui.gamebooster.gbservices;

import android.os.IBinder;
import b.b.c.f.a;
import com.miui.gamebooster.service.IFreeformWindow;

class G implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ H f4326a;

    G(H h) {
        this.f4326a = h;
    }

    public boolean a(IBinder iBinder) {
        IFreeformWindow unused = this.f4326a.j = IFreeformWindow.Stub.a(iBinder);
        return false;
    }
}
