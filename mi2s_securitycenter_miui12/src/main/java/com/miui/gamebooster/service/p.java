package com.miui.gamebooster.service;

import android.os.IBinder;
import b.b.c.f.a;
import com.miui.gamebooster.service.IFreeformWindow;

class p implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f4828a;

    p(r rVar) {
        this.f4828a = rVar;
    }

    public boolean a(IBinder iBinder) {
        IFreeformWindow unused = this.f4828a.w = IFreeformWindow.Stub.a(iBinder);
        return false;
    }
}
