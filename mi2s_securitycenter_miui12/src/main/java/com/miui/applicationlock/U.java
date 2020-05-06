package com.miui.applicationlock;

import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;
import android.util.Log;
import b.b.o.g.e;

class U implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Bundle f3222a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ UserHandle f3223b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3224c;

    U(ConfirmAccessControl confirmAccessControl, Bundle bundle, UserHandle userHandle) {
        this.f3224c = confirmAccessControl;
        this.f3222a = bundle;
        this.f3223b = userHandle;
    }

    public void run() {
        try {
            e.a((Object) this.f3224c.getBaseContext(), "startActivityAsUser", (Class<?>[]) new Class[]{Intent.class, Bundle.class, UserHandle.class}, this.f3224c.l, this.f3222a, this.f3223b);
        } catch (Exception e) {
            Log.e("ConfirmAccessControl", "post delay start activity", e);
        }
    }
}
