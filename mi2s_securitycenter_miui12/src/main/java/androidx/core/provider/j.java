package androidx.core.provider;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AtomicReference f766a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Callable f767b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ ReentrantLock f768c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ AtomicBoolean f769d;
    final /* synthetic */ Condition e;
    final /* synthetic */ k f;

    j(k kVar, AtomicReference atomicReference, Callable callable, ReentrantLock reentrantLock, AtomicBoolean atomicBoolean, Condition condition) {
        this.f = kVar;
        this.f766a = atomicReference;
        this.f767b = callable;
        this.f768c = reentrantLock;
        this.f769d = atomicBoolean;
        this.e = condition;
    }

    public void run() {
        try {
            this.f766a.set(this.f767b.call());
        } catch (Exception unused) {
        }
        this.f768c.lock();
        try {
            this.f769d.set(false);
            this.e.signal();
        } finally {
            this.f768c.unlock();
        }
    }
}
