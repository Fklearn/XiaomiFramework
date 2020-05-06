package com.miui.gamebooster.a;

import com.miui.gamebooster.globalgame.util.Utils;

/* renamed from: com.miui.gamebooster.a.g  reason: case insensitive filesystem */
class C0329g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0330h f4043a;

    C0329g(C0330h hVar) {
        this.f4043a = hVar;
    }

    public void run() {
        boolean unused = this.f4043a.f4045b.l = true;
        Utils.a(this.f4043a.f4045b.f4070d, this.f4043a.f4045b.h);
        this.f4043a.f4045b.f4069c.setAlpha(1.0f);
        Utils.c(this.f4043a.f4045b.f4068b, this.f4043a.f4045b.f4069c);
    }
}
