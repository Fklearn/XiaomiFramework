package com.miui.gamebooster.m;

import android.content.Context;
import android.os.UserHandle;

/* renamed from: com.miui.gamebooster.m.x  reason: case insensitive filesystem */
class C0392x implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f4525a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ String f4526b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ UserHandle f4527c;

    C0392x(Context context, String str, UserHandle userHandle) {
        this.f4525a = context;
        this.f4526b = str;
        this.f4527c = userHandle;
    }

    public void run() {
        C0393y.a(this.f4525a, this.f4526b, this.f4527c);
    }
}
