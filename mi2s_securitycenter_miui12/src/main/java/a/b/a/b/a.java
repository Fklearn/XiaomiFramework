package a.b.a.b;

import a.b.a.b.b;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import java.util.HashMap;
import java.util.Map;

@RestrictTo({RestrictTo.a.f224c})
public class a<K, V> extends b<K, V> {
    private HashMap<K, b.c<K, V>> e = new HashMap<>();

    /* access modifiers changed from: protected */
    public b.c<K, V> a(K k) {
        return this.e.get(k);
    }

    public V b(@NonNull K k, @NonNull V v) {
        b.c a2 = a(k);
        if (a2 != null) {
            return a2.f54b;
        }
        this.e.put(k, a(k, v));
        return null;
    }

    public Map.Entry<K, V> b(K k) {
        if (contains(k)) {
            return this.e.get(k).f56d;
        }
        return null;
    }

    public boolean contains(K k) {
        return this.e.containsKey(k);
    }

    public V remove(@NonNull K k) {
        V remove = super.remove(k);
        this.e.remove(k);
        return remove;
    }
}
