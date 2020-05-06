package com.miui.gamebooster.n.d;

import android.view.View;

public abstract class b {

    /* renamed from: a  reason: collision with root package name */
    protected String f4687a;

    /* renamed from: b  reason: collision with root package name */
    protected int f4688b;

    public interface a {
        void a(b bVar, View view);
    }

    public b(int i) {
        this.f4688b = i;
    }

    public b(String str) {
        this.f4687a = str;
    }

    public int a() {
        return this.f4688b;
    }

    public void a(View view) {
    }

    public boolean b() {
        return true;
    }

    public String toString() {
        return "BaseModel{title='" + this.f4687a + '\'' + '}';
    }
}
