package a.c;

import java.util.Map;

class a extends h<K, V> {

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ b f62d;

    a(b bVar) {
        this.f62d = bVar;
    }

    /* access modifiers changed from: protected */
    public int a(Object obj) {
        return this.f62d.a(obj);
    }

    /* access modifiers changed from: protected */
    public Object a(int i, int i2) {
        return this.f62d.f[(i << 1) + i2];
    }

    /* access modifiers changed from: protected */
    public V a(int i, V v) {
        return this.f62d.a(i, v);
    }

    /* access modifiers changed from: protected */
    public void a() {
        this.f62d.clear();
    }

    /* access modifiers changed from: protected */
    public void a(int i) {
        this.f62d.c(i);
    }

    /* access modifiers changed from: protected */
    public void a(K k, V v) {
        this.f62d.put(k, v);
    }

    /* access modifiers changed from: protected */
    public int b(Object obj) {
        return this.f62d.b(obj);
    }

    /* access modifiers changed from: protected */
    public Map<K, V> b() {
        return this.f62d;
    }

    /* access modifiers changed from: protected */
    public int c() {
        return this.f62d.g;
    }
}
