package com.miui.gamebooster.m;

import com.miui.gamebooster.model.q;
import java.util.concurrent.ConcurrentHashMap;

public class ia {

    /* renamed from: a  reason: collision with root package name */
    private static ia f4495a;

    /* renamed from: b  reason: collision with root package name */
    private ConcurrentHashMap<String, q> f4496b = new ConcurrentHashMap<>();

    public static synchronized ia a() {
        ia iaVar;
        synchronized (ia.class) {
            if (f4495a == null) {
                f4495a = new ia();
            }
            iaVar = f4495a;
        }
        return iaVar;
    }

    public q a(String str) {
        if (this.f4496b.get(str) == null) {
            this.f4496b.put(str, new q());
        }
        return this.f4496b.get(str);
    }

    public void a(String str, q qVar) {
        this.f4496b.put(str, qVar);
    }

    public void b() {
        this.f4496b.clear();
    }
}
