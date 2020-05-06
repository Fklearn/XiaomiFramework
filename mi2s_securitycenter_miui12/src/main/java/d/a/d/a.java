package d.a.d;

import android.util.ArrayMap;
import android.util.Log;
import d.a.d;
import d.a.g.C0575b;
import java.util.Map;

class a {
    private static h a(d dVar, C0575b bVar, k kVar) {
        Log.d("miuix_anim", "createAnimInfo for " + dVar + ", " + bVar.getName() + ", toTag = " + kVar.f8716c);
        h hVar = new h();
        hVar.f8706a = dVar;
        hVar.f8707b = bVar;
        hVar.a(kVar);
        hVar.b(kVar);
        hVar.a(kVar.f8715b);
        return hVar;
    }

    static Map<C0575b, h> a(d dVar, k kVar) {
        Log.d("miuix_anim", "createAnimInfo, target = " + dVar + ", tag = " + kVar.f8716c + ", from = " + d.a.i.a.a(kVar.f8717d, "    ") + ", to = " + d.a.i.a.a(kVar.e, "    "));
        ArrayMap arrayMap = new ArrayMap();
        for (C0575b next : kVar.e.keySet()) {
            arrayMap.put(next, a(dVar, next, kVar));
        }
        return arrayMap;
    }
}
