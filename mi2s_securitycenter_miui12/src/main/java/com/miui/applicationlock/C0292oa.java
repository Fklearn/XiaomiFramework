package com.miui.applicationlock;

import android.database.ContentObserver;
import android.os.Handler;
import com.miui.applicationlock.c.o;

/* renamed from: com.miui.applicationlock.oa  reason: case insensitive filesystem */
class C0292oa extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3369a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    C0292oa(ConfirmAccessControl confirmAccessControl, Handler handler) {
        super(handler);
        this.f3369a = confirmAccessControl;
    }

    public void onChange(boolean z) {
        o.a(0, this.f3369a.getApplicationContext());
    }
}
