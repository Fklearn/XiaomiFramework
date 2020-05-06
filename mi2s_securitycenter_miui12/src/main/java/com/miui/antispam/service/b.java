package com.miui.antispam.service;

import android.content.Context;

public abstract class b {

    /* renamed from: a  reason: collision with root package name */
    protected Context f2410a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f2411b = false;

    /* renamed from: c  reason: collision with root package name */
    private boolean f2412c = false;

    /* renamed from: d  reason: collision with root package name */
    private int f2413d = 0;
    private a e;

    public interface a {
        void a(b bVar);

        void b(b bVar);
    }

    public b(Context context, a aVar) {
        this.f2410a = context;
        this.e = aVar;
        b();
    }

    public abstract String a();

    public synchronized boolean a(boolean z) {
        this.f2412c = true;
        if (z) {
            c();
            return true;
        } else if (this.f2411b) {
            return false;
        } else {
            c();
            return true;
        }
    }

    public void b() {
        this.e.a(this);
    }

    /* access modifiers changed from: protected */
    public synchronized void b(boolean z) {
        if (z) {
            this.f2411b = true;
            this.f2413d++;
        } else {
            this.f2413d--;
            if (this.f2412c && this.f2413d <= 0) {
                this.f2411b = false;
                a(true);
            }
        }
    }

    public void c() {
        this.e.b(this);
    }
}
