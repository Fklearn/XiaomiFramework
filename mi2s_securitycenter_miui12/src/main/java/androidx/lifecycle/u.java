package androidx.lifecycle;

import java.util.HashMap;

public class u {

    /* renamed from: a  reason: collision with root package name */
    private final HashMap<String, s> f1005a = new HashMap<>();

    /* access modifiers changed from: package-private */
    public final s a(String str) {
        return this.f1005a.get(str);
    }

    public final void a() {
        for (s a2 : this.f1005a.values()) {
            a2.a();
        }
        this.f1005a.clear();
    }

    /* access modifiers changed from: package-private */
    public final void a(String str, s sVar) {
        s put = this.f1005a.put(str, sVar);
        if (put != null) {
            put.b();
        }
    }
}
