package com.miui.gamebooster.a;

import com.miui.gamebooster.globalgame.util.Utils;

class p implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Runnable f4056a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ v f4057b;

    p(v vVar, Runnable runnable) {
        this.f4057b = vVar;
        this.f4056a = runnable;
    }

    public void run() {
        Utils.a(this.f4056a);
    }
}
