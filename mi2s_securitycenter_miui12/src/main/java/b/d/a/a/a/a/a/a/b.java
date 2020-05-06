package b.d.a.a.a.a.a.a;

import b.d.a.a.b.c;
import b.d.a.a.d.a.d;
import b.d.a.a.d.a.g;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class b extends a {
    public c a(String str) {
        int i;
        if (str == null || str.equals("")) {
            throw new RuntimeException("smsBody is null or empty!");
        }
        HashMap hashMap = new HashMap();
        Iterator<g> it = a.f2082b.a(str, d.a.INDEX).iterator();
        while (true) {
            i = 0;
            if (!it.hasNext()) {
                break;
            }
            g next = it.next();
            if (a.f2081a.containsKey(next.f2130a)) {
                Integer num = a.f2081a.get(next.f2130a);
                if (hashMap.containsKey(num)) {
                    i = ((Integer) hashMap.get(num)).intValue();
                }
                hashMap.put(num, Integer.valueOf(i + 1));
            }
        }
        int[] iArr = new int[hashMap.size()];
        float[] fArr = new float[hashMap.size()];
        for (Map.Entry entry : hashMap.entrySet()) {
            iArr[i] = ((Integer) entry.getKey()).intValue();
            fArr[i] = (float) ((Integer) entry.getValue()).intValue();
            i++;
        }
        return new b.d.a.a.b.b(a.f2083c, iArr, fArr);
    }
}
