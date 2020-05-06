package b.d.a.a.a.a.a;

import b.d.a.a.a.a.a.a.b;
import b.d.a.a.a.a.a.a.c;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static Map<b.d.a.a.a.a, b.d.a.a.a.a.a> f2080a = new ConcurrentHashMap();

    public static b.d.a.a.a.a.a a(b.d.a.a.a.a aVar) {
        if (f2080a.containsKey(aVar)) {
            return f2080a.get(aVar);
        }
        b.d.a.a.a.a.a cVar = b.d.a.a.a.a.TELECOM_OPERATOR.equals(aVar) ? new c() : new b();
        f2080a.put(aVar, cVar);
        return cVar;
    }

    public static boolean a() {
        f2080a = new ConcurrentHashMap();
        return true;
    }
}
