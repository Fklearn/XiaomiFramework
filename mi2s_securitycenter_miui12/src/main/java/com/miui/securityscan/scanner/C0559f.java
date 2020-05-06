package com.miui.securityscan.scanner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/* renamed from: com.miui.securityscan.scanner.f  reason: case insensitive filesystem */
public class C0559f {

    /* renamed from: a  reason: collision with root package name */
    private Map<v, BlockingQueue<C0558e>> f7893a = new ConcurrentHashMap();

    public C0559f() {
        for (v put : v.values()) {
            this.f7893a.put(put, new LinkedBlockingQueue());
        }
    }

    /* access modifiers changed from: protected */
    public BlockingQueue<C0558e> a(v vVar) {
        return this.f7893a.get(vVar);
    }

    /* access modifiers changed from: protected */
    public void a() {
        for (v vVar : v.values()) {
            this.f7893a.get(vVar).clear();
        }
    }

    /* access modifiers changed from: protected */
    public void a(v vVar, C0558e eVar) {
        this.f7893a.get(vVar).put(eVar);
    }
}
