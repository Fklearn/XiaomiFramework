package com.miui.powercenter.autotask;

import android.content.Intent;
import android.os.Bundle;

abstract class A<T> {

    /* renamed from: a  reason: collision with root package name */
    protected AutoTask f6670a;

    /* renamed from: b  reason: collision with root package name */
    protected AutoTask f6671b;

    /* renamed from: c  reason: collision with root package name */
    protected T f6672c;

    public A(AutoTask autoTask, AutoTask autoTask2) {
        this.f6670a = autoTask;
        this.f6671b = autoTask2;
    }

    public abstract void a(int i, int i2, Intent intent);

    public abstract void a(Bundle bundle);

    public void a(T t) {
        this.f6672c = t;
    }

    public abstract void b(Bundle bundle);
}
