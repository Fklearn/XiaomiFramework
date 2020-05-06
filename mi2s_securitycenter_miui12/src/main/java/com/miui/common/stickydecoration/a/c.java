package com.miui.common.stickydecoration.a;

import android.app.StatusBarManager;
import android.util.LruCache;

public class c<T> implements a<T> {

    /* renamed from: a  reason: collision with root package name */
    private boolean f3838a = true;

    /* renamed from: b  reason: collision with root package name */
    private LruCache<Integer, T> f3839b;

    public c() {
        a();
    }

    private void a() {
        this.f3839b = new b(this, StatusBarManager.DISABLE_HOME);
    }

    public T a(int i) {
        if (!this.f3838a) {
            return null;
        }
        return this.f3839b.get(Integer.valueOf(i));
    }

    public void a(int i, T t) {
        if (this.f3838a) {
            this.f3839b.put(Integer.valueOf(i), t);
        }
    }
}
