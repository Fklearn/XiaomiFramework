package com.miui.securityscan.scanner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/* renamed from: com.miui.securityscan.scanner.n  reason: case insensitive filesystem */
public class C0567n {

    /* renamed from: a  reason: collision with root package name */
    private Map<C0568o, BlockingQueue<C0558e>> f7909a = new ConcurrentHashMap();

    public C0567n() {
        for (C0568o put : C0568o.values()) {
            this.f7909a.put(put, new LinkedBlockingQueue());
        }
    }

    /* access modifiers changed from: protected */
    public BlockingQueue<C0558e> a(C0568o oVar) {
        return this.f7909a.get(oVar);
    }

    /* access modifiers changed from: protected */
    public void a() {
        for (C0568o oVar : C0568o.values()) {
            this.f7909a.get(oVar).clear();
        }
    }

    /* access modifiers changed from: protected */
    public void a(C0568o oVar, C0558e eVar) {
        this.f7909a.get(oVar).put(eVar);
    }
}
