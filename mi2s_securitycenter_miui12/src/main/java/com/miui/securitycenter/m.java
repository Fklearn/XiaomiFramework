package com.miui.securitycenter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class m implements ThreadFactory {

    /* renamed from: a  reason: collision with root package name */
    private final AtomicInteger f7484a = new AtomicInteger(1);

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ n f7485b;

    m(n nVar) {
        this.f7485b = nVar;
    }

    public Thread newThread(Runnable runnable) {
        l lVar = new l(this, runnable);
        return new Thread(lVar, "SCLIGHTTask #" + this.f7484a.getAndIncrement());
    }
}
