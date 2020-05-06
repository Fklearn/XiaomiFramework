package com.miui.superpower.a;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class k implements d {

    /* renamed from: a  reason: collision with root package name */
    protected Context f8065a;

    /* renamed from: b  reason: collision with root package name */
    protected SharedPreferences f8066b;

    public k(Context context, SharedPreferences sharedPreferences) {
        this.f8065a = context.getApplicationContext();
        this.f8066b = sharedPreferences;
    }

    public boolean a() {
        return false;
    }

    public void b() {
    }

    public void c() {
    }

    public void d() {
    }

    public String name() {
        return getClass().getSimpleName();
    }
}
