package com.miui.securitycenter;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class k implements ThreadFactory {

    /* renamed from: a  reason: collision with root package name */
    private final AtomicInteger f7480a = new AtomicInteger(1);

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ n f7481b;

    k(n nVar) {
        this.f7481b = nVar;
    }

    public Thread newThread(Runnable runnable) {
        j jVar = new j(this, runnable);
        return new Thread(jVar, "SCHeavyTask #" + this.f7480a.getAndIncrement());
    }
}
