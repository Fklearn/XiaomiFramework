package com.miui.firstaidkit;

import com.miui.securityscan.scanner.C0558e;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class m {

    /* renamed from: a  reason: collision with root package name */
    private Map<n, BlockingQueue<C0558e>> f3957a = new ConcurrentHashMap();

    public m() {
        for (n put : n.values()) {
            this.f3957a.put(put, new LinkedBlockingQueue());
        }
    }

    /* access modifiers changed from: protected */
    public BlockingQueue<C0558e> a(n nVar) {
        return this.f3957a.get(nVar);
    }

    /* access modifiers changed from: protected */
    public void a() {
        for (n nVar : n.values()) {
            this.f3957a.get(nVar).clear();
        }
    }

    /* access modifiers changed from: protected */
    public void a(n nVar, C0558e eVar) {
        this.f3957a.get(nVar).put(eVar);
    }
}
