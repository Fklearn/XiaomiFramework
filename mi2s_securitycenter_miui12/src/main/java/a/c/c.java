package a.c;

import java.util.Map;

class c extends h<E, E> {

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ d f63d;

    c(d dVar) {
        this.f63d = dVar;
    }

    /* access modifiers changed from: protected */
    public int a(Object obj) {
        return this.f63d.indexOf(obj);
    }

    /* access modifiers changed from: protected */
    public Object a(int i, int i2) {
        return this.f63d.h[i];
    }

    /* access modifiers changed from: protected */
    public E a(int i, E e) {
        throw new UnsupportedOperationException("not a map");
    }

    /* access modifiers changed from: protected */
    public void a() {
        this.f63d.clear();
    }

    /* access modifiers changed from: protected */
    public void a(int i) {
        this.f63d.b(i);
    }

    /* access modifiers changed from: protected */
    public void a(E e, E e2) {
        this.f63d.add(e);
    }

    /* access modifiers changed from: protected */
    public int b(Object obj) {
        return this.f63d.indexOf(obj);
    }

    /* access modifiers changed from: protected */
    public Map<E, E> b() {
        throw new UnsupportedOperationException("not a map");
    }

    /* access modifiers changed from: protected */
    public int c() {
        return this.f63d.i;
    }
}
