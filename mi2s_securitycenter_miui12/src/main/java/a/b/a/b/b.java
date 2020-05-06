package a.b.a.b;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@RestrictTo({RestrictTo.a.f224c})
public class b<K, V> implements Iterable<Map.Entry<K, V>> {

    /* renamed from: a  reason: collision with root package name */
    c<K, V> f49a;

    /* renamed from: b  reason: collision with root package name */
    private c<K, V> f50b;

    /* renamed from: c  reason: collision with root package name */
    private WeakHashMap<f<K, V>, Boolean> f51c = new WeakHashMap<>();

    /* renamed from: d  reason: collision with root package name */
    private int f52d = 0;

    static class a<K, V> extends e<K, V> {
        a(c<K, V> cVar, c<K, V> cVar2) {
            super(cVar, cVar2);
        }

        /* access modifiers changed from: package-private */
        public c<K, V> b(c<K, V> cVar) {
            return cVar.f56d;
        }

        /* access modifiers changed from: package-private */
        public c<K, V> c(c<K, V> cVar) {
            return cVar.f55c;
        }
    }

    /* renamed from: a.b.a.b.b$b  reason: collision with other inner class name */
    private static class C0001b<K, V> extends e<K, V> {
        C0001b(c<K, V> cVar, c<K, V> cVar2) {
            super(cVar, cVar2);
        }

        /* access modifiers changed from: package-private */
        public c<K, V> b(c<K, V> cVar) {
            return cVar.f55c;
        }

        /* access modifiers changed from: package-private */
        public c<K, V> c(c<K, V> cVar) {
            return cVar.f56d;
        }
    }

    static class c<K, V> implements Map.Entry<K, V> {
        @NonNull

        /* renamed from: a  reason: collision with root package name */
        final K f53a;
        @NonNull

        /* renamed from: b  reason: collision with root package name */
        final V f54b;

        /* renamed from: c  reason: collision with root package name */
        c<K, V> f55c;

        /* renamed from: d  reason: collision with root package name */
        c<K, V> f56d;

        c(@NonNull K k, @NonNull V v) {
            this.f53a = k;
            this.f54b = v;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof c)) {
                return false;
            }
            c cVar = (c) obj;
            return this.f53a.equals(cVar.f53a) && this.f54b.equals(cVar.f54b);
        }

        @NonNull
        public K getKey() {
            return this.f53a;
        }

        @NonNull
        public V getValue() {
            return this.f54b;
        }

        public int hashCode() {
            return this.f53a.hashCode() ^ this.f54b.hashCode();
        }

        public V setValue(V v) {
            throw new UnsupportedOperationException("An entry modification is not supported");
        }

        public String toString() {
            return this.f53a + "=" + this.f54b;
        }
    }

    private class d implements Iterator<Map.Entry<K, V>>, f<K, V> {

        /* renamed from: a  reason: collision with root package name */
        private c<K, V> f57a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f58b = true;

        d() {
        }

        public void a(@NonNull c<K, V> cVar) {
            c<K, V> cVar2 = this.f57a;
            if (cVar == cVar2) {
                this.f57a = cVar2.f56d;
                this.f58b = this.f57a == null;
            }
        }

        public boolean hasNext() {
            if (this.f58b) {
                return b.this.f49a != null;
            }
            c<K, V> cVar = this.f57a;
            return (cVar == null || cVar.f55c == null) ? false : true;
        }

        public Map.Entry<K, V> next() {
            c<K, V> cVar;
            if (this.f58b) {
                this.f58b = false;
                cVar = b.this.f49a;
            } else {
                c<K, V> cVar2 = this.f57a;
                cVar = cVar2 != null ? cVar2.f55c : null;
            }
            this.f57a = cVar;
            return this.f57a;
        }
    }

    private static abstract class e<K, V> implements Iterator<Map.Entry<K, V>>, f<K, V> {

        /* renamed from: a  reason: collision with root package name */
        c<K, V> f60a;

        /* renamed from: b  reason: collision with root package name */
        c<K, V> f61b;

        e(c<K, V> cVar, c<K, V> cVar2) {
            this.f60a = cVar2;
            this.f61b = cVar;
        }

        private c<K, V> a() {
            c<K, V> cVar = this.f61b;
            c<K, V> cVar2 = this.f60a;
            if (cVar == cVar2 || cVar2 == null) {
                return null;
            }
            return c(cVar);
        }

        public void a(@NonNull c<K, V> cVar) {
            if (this.f60a == cVar && cVar == this.f61b) {
                this.f61b = null;
                this.f60a = null;
            }
            c<K, V> cVar2 = this.f60a;
            if (cVar2 == cVar) {
                this.f60a = b(cVar2);
            }
            if (this.f61b == cVar) {
                this.f61b = a();
            }
        }

        /* access modifiers changed from: package-private */
        public abstract c<K, V> b(c<K, V> cVar);

        /* access modifiers changed from: package-private */
        public abstract c<K, V> c(c<K, V> cVar);

        public boolean hasNext() {
            return this.f61b != null;
        }

        public Map.Entry<K, V> next() {
            c<K, V> cVar = this.f61b;
            this.f61b = a();
            return cVar;
        }
    }

    interface f<K, V> {
        void a(@NonNull c<K, V> cVar);
    }

    /* access modifiers changed from: protected */
    public c<K, V> a(K k) {
        c<K, V> cVar = this.f49a;
        while (cVar != null && !cVar.f53a.equals(k)) {
            cVar = cVar.f55c;
        }
        return cVar;
    }

    /* access modifiers changed from: protected */
    public c<K, V> a(@NonNull K k, @NonNull V v) {
        c<K, V> cVar = new c<>(k, v);
        this.f52d++;
        c<K, V> cVar2 = this.f50b;
        if (cVar2 == null) {
            this.f49a = cVar;
            this.f50b = this.f49a;
            return cVar;
        }
        cVar2.f55c = cVar;
        cVar.f56d = cVar2;
        this.f50b = cVar;
        return cVar;
    }

    public Map.Entry<K, V> a() {
        return this.f49a;
    }

    public b<K, V>.d b() {
        b<K, V>.d dVar = new d();
        this.f51c.put(dVar, false);
        return dVar;
    }

    public V b(@NonNull K k, @NonNull V v) {
        c a2 = a(k);
        if (a2 != null) {
            return a2.f54b;
        }
        a(k, v);
        return null;
    }

    public Map.Entry<K, V> c() {
        return this.f50b;
    }

    public Iterator<Map.Entry<K, V>> descendingIterator() {
        C0001b bVar = new C0001b(this.f50b, this.f49a);
        this.f51c.put(bVar, false);
        return bVar;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        if (size() != bVar.size()) {
            return false;
        }
        Iterator it = iterator();
        Iterator it2 = bVar.iterator();
        while (it.hasNext() && it2.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object next = it2.next();
            if ((entry == null && next != null) || (entry != null && !entry.equals(next))) {
                return false;
            }
        }
        return !it.hasNext() && !it2.hasNext();
    }

    public int hashCode() {
        Iterator it = iterator();
        int i = 0;
        while (it.hasNext()) {
            i += ((Map.Entry) it.next()).hashCode();
        }
        return i;
    }

    @NonNull
    public Iterator<Map.Entry<K, V>> iterator() {
        a aVar = new a(this.f49a, this.f50b);
        this.f51c.put(aVar, false);
        return aVar;
    }

    public V remove(@NonNull K k) {
        c a2 = a(k);
        if (a2 == null) {
            return null;
        }
        this.f52d--;
        if (!this.f51c.isEmpty()) {
            for (f<K, V> a3 : this.f51c.keySet()) {
                a3.a(a2);
            }
        }
        c<K, V> cVar = a2.f56d;
        if (cVar != null) {
            cVar.f55c = a2.f55c;
        } else {
            this.f49a = a2.f55c;
        }
        c<K, V> cVar2 = a2.f55c;
        if (cVar2 != null) {
            cVar2.f56d = a2.f56d;
        } else {
            this.f50b = a2.f56d;
        }
        a2.f55c = null;
        a2.f56d = null;
        return a2.f54b;
    }

    public int size() {
        return this.f52d;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Iterator it = iterator();
        while (it.hasNext()) {
            sb.append(((Map.Entry) it.next()).toString());
            if (it.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
