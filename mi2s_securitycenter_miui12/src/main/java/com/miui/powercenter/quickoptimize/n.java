package com.miui.powercenter.quickoptimize;

import java.util.ArrayList;
import java.util.List;

public class n {

    /* renamed from: a  reason: collision with root package name */
    private int f7234a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f7235b = false;

    /* renamed from: c  reason: collision with root package name */
    private List<m> f7236c = new ArrayList();

    public int a() {
        return this.f7236c.size();
    }

    public m a(int i) {
        return this.f7236c.get(i);
    }

    public void a(m mVar) {
        this.f7236c.add(mVar);
    }

    public void a(boolean z) {
        this.f7235b = z;
    }

    public int b() {
        return this.f7234a;
    }

    public void b(int i) {
        this.f7234a = i;
    }

    public boolean c() {
        return this.f7235b;
    }
}
