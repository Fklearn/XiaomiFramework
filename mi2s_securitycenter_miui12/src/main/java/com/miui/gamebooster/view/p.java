package com.miui.gamebooster.view;

import com.miui.gamebooster.view.n;

class p implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ n.b f5323a;

    p(n.b bVar) {
        this.f5323a = bVar;
    }

    public void run() {
        this.f5323a.f5321c.animate().scaleX(1.03f).scaleY(1.03f).setDuration(250).start();
        boolean unused = this.f5323a.f5319a = true;
    }
}
